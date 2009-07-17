package org.apache.maven.artifact.repository;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.wagon.repository.Repository;

/**
 * This class is an abstraction of the location from/to resources can be
 * transfered.
 *
 * @author <a href="michal.maczka@dimatics.com">Michal Maczka </a>
 * @version $Id$
 */
@Deprecated
public class DefaultArtifactRepository
    extends Repository
    implements ArtifactRepository
{
    private ArtifactRepositoryLayout layout;

    private ArtifactRepositoryPolicy snapshots;

    private ArtifactRepositoryPolicy releases;

    private boolean blacklisted;
    
    /**
     * Create a local repository or a test repository.
     *
     * @param id     the unique identifier of the repository
     * @param url    the URL of the repository
     * @param layout the layout of the repository
     */
    public DefaultArtifactRepository( String id, String url, ArtifactRepositoryLayout layout )
    {
        this( id, url, layout, null, null );
    }

    /**
     * Create a remote deployment repository.
     *
     * @param id            the unique identifier of the repository
     * @param url           the URL of the repository
     * @param layout        the layout of the repository
     * @param uniqueVersion whether to assign each snapshot a unique version
     */
    public DefaultArtifactRepository( String id, String url, ArtifactRepositoryLayout layout, boolean uniqueVersion )
    {
        super( id, url );
        this.layout = layout;
    }

    /**
     * Create a remote download repository.
     *
     * @param id        the unique identifier of the repository
     * @param url       the URL of the repository
     * @param layout    the layout of the repository
     * @param snapshots the policies to use for snapshots
     * @param releases  the policies to use for releases
     */
    public DefaultArtifactRepository( String id, String url, ArtifactRepositoryLayout layout, ArtifactRepositoryPolicy snapshots, ArtifactRepositoryPolicy releases )
    {
        super( id, url );

        this.layout = layout;

        if ( snapshots == null )
        {
            snapshots = new ArtifactRepositoryPolicy( true, ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS,
                ArtifactRepositoryPolicy.CHECKSUM_POLICY_IGNORE );
        }

        this.snapshots = snapshots;

        if ( releases == null )
        {
            releases = new ArtifactRepositoryPolicy( true, ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS,
                ArtifactRepositoryPolicy.CHECKSUM_POLICY_IGNORE );
        }

        this.releases = releases;
    }

    public String pathOf( Artifact artifact )
    {
        return layout.pathOf( artifact );
    }

    public String pathOfRemoteRepositoryMetadata( ArtifactMetadata artifactMetadata )
    {
        return layout.pathOfRemoteRepositoryMetadata( artifactMetadata );
    }

    public String pathOfLocalRepositoryMetadata( ArtifactMetadata metadata,
                                                 ArtifactRepository repository )
    {
        return layout.pathOfLocalRepositoryMetadata( metadata, repository );
    }

    public void setLayout( ArtifactRepositoryLayout layout )
    {
        this.layout = layout;
    }
    
    public ArtifactRepositoryLayout getLayout()
    {
        return layout;
    }

    public void setSnapshotUpdatePolicy( ArtifactRepositoryPolicy snapshots )
    {
        this.snapshots = snapshots;
    }
    
    public ArtifactRepositoryPolicy getSnapshots()
    {
        return snapshots;
    }

    public void setReleaseUpdatePolicy( ArtifactRepositoryPolicy releases )
    {
        this.releases = releases;
    }

    public ArtifactRepositoryPolicy getReleases()
    {
        return releases;
    }

    public String getKey()
    {
        return getId();
    }

    public boolean isBlacklisted()
    {
        return blacklisted;
    }

    public void setBlacklisted( boolean blacklisted )
    {
        this.blacklisted = blacklisted;
    }
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        sb.append( "       id: " ).append( getId() ).append( "\n" );
        sb.append( "      url: " ).append( getUrl() ).append( "\n" );
        sb.append( "   layout: " ).append( layout != null ? layout.getId() : "none" ).append( "\n" );
        
        if ( snapshots != null )
        {
            sb.append( "snapshots: [enabled => " ).append( snapshots.isEnabled() );
            sb.append( ", update => " ).append( snapshots.getUpdatePolicy() ).append( "]\n" );
        }
        
        if ( releases != null )
        {
            sb.append( " releases: [enabled => " ).append( releases.isEnabled() );
            sb.append( ", update => " ).append( releases.getUpdatePolicy() ).append( "]\n" );
        }

        return sb.toString();
    }

    public Artifact find( Artifact artifact )
    {
        File artifactFile = new File( getBasedir(), pathOf( artifact ) );
        
        // We need to set the file here or the resolver will fail with an NPE, not fully equipped to deal
        // with multiple local repository implementations yet.
        artifact.setFile( artifactFile );
        
        if( artifactFile.exists() )
        {            
            artifact.setResolved( true );            
        }
                
        return artifact;
    }

    //
    // This implementation does not support authentication
    //
    public Authentication getAuthentication()
    {
        return null;
    }

    public void setAuthentication( Authentication authentication )
    {
        // do nothing
    }
}