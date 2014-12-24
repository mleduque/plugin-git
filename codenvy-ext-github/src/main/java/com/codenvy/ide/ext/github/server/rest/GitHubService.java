/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.github.server.rest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.codenvy.ide.commons.ParsingResponseException;
import com.codenvy.ide.ext.git.server.GitException;
import com.codenvy.ide.ext.github.server.GitHub;
import com.codenvy.ide.ext.github.server.GitHubException;
import com.codenvy.ide.ext.github.server.GitHubKeyUploader;
import com.codenvy.ide.ext.github.shared.Collaborators;
import com.codenvy.ide.ext.github.shared.GitHubPullRequest;
import com.codenvy.ide.ext.github.shared.GitHubPullRequestInput;
import com.codenvy.ide.ext.github.shared.GitHubRepository;
import com.codenvy.ide.ext.github.shared.GitHubRepositoryList;
import com.codenvy.ide.ext.github.shared.GitHubUser;
import com.codenvy.ide.ext.ssh.server.SshKey;
import com.codenvy.ide.ext.ssh.server.SshKeyStore;
import com.codenvy.ide.ext.ssh.server.SshKeyStoreException;

/**
 * REST service to get the list of repositories from GitHub (where sample projects are located).
 *
 * @author Oksana Vereshchaka
 * @author Stéphane Daviet
 */
@Path("github")
public class GitHubService {
    @Inject
    GitHub github;

    @Inject
    GitHubKeyUploader githubKeyUploader;

    @Inject
    SshKeyStore sshKeyStore;

    @Path("list/user")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public GitHubRepositoryList listRepositoriesByUser(@QueryParam("username") String userName)
            throws IOException, GitHubException, ParsingResponseException {
        return github.listUserPublicRepositories(userName);
    }

    @Path("list/org")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public GitHubRepositoryList listRepositoriesByOrganization(@QueryParam("organization") String organization)
            throws IOException, GitHubException, ParsingResponseException {
        return github.listAllOrganizationRepositories(organization);
    }

    @Path("list/account")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public GitHubRepositoryList listRepositoriesByAccount(@QueryParam("account") String account)
            throws IOException, GitHubException, ParsingResponseException {
        try {
            //First, try to retrieve organization repositories:
            return github.listAllOrganizationRepositories(account);
        } catch (final GitHubException ghe) {
            //If account is not organization, then try by user name:
            if (ghe.getResponseStatus() == 404) {
                return github.listUserPublicRepositories(account);
            } else {
                throw ghe;
            }
        }
    }

    @Path("list")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public GitHubRepositoryList listRepositories() throws IOException, GitHubException, ParsingResponseException {
        return github.listCurrentUserRepositories();
    }

    @Path("forks/{user}/{repository}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public GitHubRepositoryList forks(@PathParam("user") String user, @PathParam("repository") String repository)
            throws IOException, GitHubException, ParsingResponseException {
        return github.getForks(user, repository);
    }

    @Path("createfork/{user}/{repository}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public GitHubRepository fork(@PathParam("user") String user, @PathParam("repository") String repository)
            throws IOException, GitHubException, ParsingResponseException {
        return github.fork(user, repository);
    }

    @Path("pullrequest/{user}/{repository}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public GitHubPullRequest createPullRequest(@PathParam("user") String user, @PathParam("repository") String repository, GitHubPullRequestInput input)
            throws IOException, GitHubException, ParsingResponseException {
        return github.createPullRequest(user, repository, input);
    }

    @Path("pullrequest/{user}/{repository}/{number}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public GitHubPullRequest getPullRequest(@PathParam("user") String user, @PathParam("repository") String repository, @PathParam("number") int number)
            throws IOException, GitHubException, ParsingResponseException {
        return github.getPullRequest(user, repository, number);
    }

    @Path("list/available")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, List<GitHubRepository>> availableRepositories() throws IOException, GitHubException, ParsingResponseException {
        return github.availableRepositoriesList();
    }

    @Path("page")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public GitHubRepositoryList getPage(@QueryParam("url") String url) throws IOException, GitHubException, ParsingResponseException {
        return github.getPage(url);
    }

    @Path("orgs")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> listOrganizations() throws IOException, GitHubException, ParsingResponseException {
        return github.listOrganizations();
    }

    @Path("user")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public GitHubUser getUserInfo() throws IOException, GitHubException, ParsingResponseException {
        return github.getGithubUser();
    }

    @GET
    @Path("collaborators/{user}/{repository}")
    @Produces(MediaType.APPLICATION_JSON)
    public Collaborators collaborators(@PathParam("user") String user, @PathParam("repository") String repository)
            throws IOException, GitHubException, ParsingResponseException {
        return github.getCollaborators(user, repository);
    }

    @GET
    @Path("token/{userid}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getToken(@PathParam("userid") String userId) throws IOException, GitHubException, ParsingResponseException {
        return github.getToken(userId);
    }

    @POST
    @Path("ssh/generate")
    public void updateSSHKey() throws Exception {
        final String host = "github.com";
        SshKey publicKey;

        try {
            if (sshKeyStore.getPrivateKey(host) != null) {
                publicKey = sshKeyStore.getPublicKey(host);
                if (publicKey == null) {
                    sshKeyStore.removeKeys(host);
                    publicKey = sshKeyStore.genKeyPair(host, null, null).getPublicKey();
                }
            } else {
                publicKey = sshKeyStore.genKeyPair(host, null, null).getPublicKey();
            }
        } catch (final SshKeyStoreException e) {
            throw new GitException(e.getMessage(), e);
        }

        // update public key
        try {
            githubKeyUploader.uploadKey(publicKey);
        } catch (final IOException e) {
            throw new GitException(e.getMessage(), e);
        }
    }
}
