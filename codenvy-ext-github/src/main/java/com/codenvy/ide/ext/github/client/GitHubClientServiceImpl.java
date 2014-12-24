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
package com.codenvy.ide.ext.github.client;

import java.util.List;

import javax.annotation.Nonnull;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.ext.github.shared.Collaborators;
import com.codenvy.ide.ext.github.shared.GitHubPullRequest;
import com.codenvy.ide.ext.github.shared.GitHubPullRequestInput;
import com.codenvy.ide.ext.github.shared.GitHubRepository;
import com.codenvy.ide.ext.github.shared.GitHubRepositoryList;
import com.codenvy.ide.ext.github.shared.GitHubUser;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.AsyncRequestFactory;
import com.codenvy.ide.rest.AsyncRequestLoader;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * Implementation for {@link GitHubClientService}.
 *
 * @author Oksana Vereshchaka
 * @author Stéphane Daviet
 */
@Singleton
public class GitHubClientServiceImpl implements GitHubClientService {
    private static final String       LIST                = "/list";
    private static final String       LIST_ACCOUNT        = "/list/account";
    private static final String       LIST_ORG            = "/list/org";
    private static final String       LIST_USER           = "/list/user";
    private static final String       LIST_ALL            = "/list/available";
    private static final String       COLLABORATORS       = "/collaborators";
    private static final String       ORGANIZATIONS       = "/orgs";
    private static final String       PAGE                = "/page";
    private static final String       TOKEN               = "/token";
    private static final String       USER                = "/user";
    private static final String       SSH_GEN             = "/ssh/generate";
    private static final String       FORKS               = "/forks";
    private static final String       CREATE_FORK         = "/createfork";
    private static final String       PULL_REQUEST = "/pullrequest";
    /** REST service context. */
    private final String              baseUrl;
    /** Loader to be displayed. */
    private final AsyncRequestLoader  loader;
    private final AsyncRequestFactory asyncRequestFactory;

    @Inject
    protected GitHubClientServiceImpl(@Named("restContext") String baseUrl,
                                      AsyncRequestLoader loader,
                                      AsyncRequestFactory asyncRequestFactory) {
        this.baseUrl = baseUrl + "/github";
        this.loader = loader;
        this.asyncRequestFactory = asyncRequestFactory;
    }

    /** {@inheritDoc} */
    @Override
    public void getRepositoriesList(AsyncRequestCallback<GitHubRepositoryList> callback) {
        final String url = baseUrl + LIST;
        asyncRequestFactory.createGetRequest(url).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getForks(@Nonnull String user, @Nonnull String repository,
                                  @Nonnull AsyncRequestCallback<GitHubRepositoryList> callback) {
        final String url = baseUrl + FORKS + "/" + user + "/" + repository;
        asyncRequestFactory.createGetRequest(url).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void fork(@Nonnull String user, @Nonnull String repository, @Nonnull AsyncRequestCallback<GitHubRepository> callback) {
        final String url = baseUrl + CREATE_FORK + "/" + user + "/" + repository;
        asyncRequestFactory.createGetRequest(url).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void createPullRequest(@Nonnull String user, @Nonnull String repository, @Nonnull GitHubPullRequestInput input,
                                  @Nonnull AsyncRequestCallback<GitHubPullRequest> callback) {
        final String url = baseUrl + PULL_REQUEST + "/" + user + "/" + repository;
        asyncRequestFactory.createPostRequest(url, input).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getPullRequest(@Nonnull String user, @Nonnull String repository, int pullRequestNumber,
                                      @Nonnull AsyncRequestCallback<GitHubPullRequest> callback) {
        final String url = baseUrl + PULL_REQUEST + "/" + user + "/" + repository + "/" + Integer.toString(pullRequestNumber);
        asyncRequestFactory.createGetRequest(url).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getRepositoriesByUser(String userName, AsyncRequestCallback<GitHubRepositoryList> callback) {
        final String params = (userName != null) ? "?username=" + userName : "";
        final String url = baseUrl + LIST_USER;
        asyncRequestFactory.createGetRequest(url + params).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getAllRepositories(AsyncRequestCallback<StringMap<Array<GitHubRepository>>> callback) {
        final String url = baseUrl + LIST_ALL;
        asyncRequestFactory.createGetRequest(url).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getCollaborators(String user, String repository, AsyncRequestCallback<Collaborators> callback) {
        final String url = baseUrl + COLLABORATORS + "/" + user + "/" + repository;
        asyncRequestFactory.createGetRequest(url).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getUserToken(@Nonnull String user, @Nonnull AsyncRequestCallback<String> callback) {
        final String url = baseUrl + TOKEN + "/" + user;
        asyncRequestFactory.createGetRequest(url).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getOrganizations(AsyncRequestCallback<List<String>> callback) {
        final String url = baseUrl + ORGANIZATIONS;
        asyncRequestFactory.createGetRequest(url).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getUserInfo(AsyncRequestCallback<GitHubUser> callback) {
        final String url = baseUrl + USER;
        asyncRequestFactory.createGetRequest(url).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getRepositoriesByOrganization(String organization, AsyncRequestCallback<GitHubRepositoryList> callback) {
        final String params = (organization != null) ? "?organization=" + organization : "";
        final String url = baseUrl + LIST_ORG;
        asyncRequestFactory.createGetRequest(url + params).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getRepositoriesByAccount(String account, AsyncRequestCallback<GitHubRepositoryList> callback) {
        final String params = (account != null) ? "?account=" + account : "";
        final String url = baseUrl + LIST_ACCOUNT;
        asyncRequestFactory.createGetRequest(url + params).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getPage(String pageLocation, AsyncRequestCallback<GitHubRepositoryList> callback) {
        final String params = (pageLocation != null) ? "?url=" + pageLocation : "";
        final String url = baseUrl + PAGE;
        asyncRequestFactory.createGetRequest(url + params).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void updatePublicKey(@Nonnull AsyncRequestCallback<Void> callback) {
        final String url = baseUrl + SSH_GEN;
        asyncRequestFactory.createPostRequest(url, null).loader(loader).send(callback);
    }
}