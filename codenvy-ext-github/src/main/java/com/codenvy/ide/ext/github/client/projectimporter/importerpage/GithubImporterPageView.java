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
package com.codenvy.ide.ext.github.client.projectimporter.importerpage;

import com.codenvy.ide.api.projectimporter.basepage.ImporterBasePageView;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ext.github.client.load.ProjectData;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.ImplementedBy;

import javax.annotation.Nonnull;

/**
 * @author Roman Nikitenko
 */
@ImplementedBy(GithubImporterPageViewImpl.class)
public interface GithubImporterPageView extends ImporterBasePageView {

    public interface ActionDelegate extends ImporterBasePageView.ActionDelegate{
        /** Performs any actions appropriate in response to the user having clicked the 'LoadRepo' key. */
        void onLoadRepoClicked();
        /**
         * Performs any actions appropriate in response to the user having selected a repository.
         *
         * @param repository
         *         selected repository
         */
        void onRepositorySelected(@Nonnull ProjectData repository);

        /** Performs any actions appropriate in response to the user having changed account field. */
        void onAccountChanged();
    }
    Widget asWidget();

    /** Sets the delegate to receive events from this view. */
    void setDelegate(ActionDelegate delegate);

    /**
     * Set available repositories for account.
     *
     * @param repositories
     *         available repositories
     */
    void setRepositories(@Nonnull Array<ProjectData> repositories);

    /**
     * Set the project's URL.
     *
     * @param url
     *         the project's URL to set
     */
    void setProjectUrl(String url);

    /** @return account name */
    @Nonnull
    String getAccountName();

    /**
     * Set available account names.
     *
     * @param names
     *         available names
     */
    void setAccountNames(@Nonnull Array<String> names);

    /** Close github panel. */
    void closeGithubPanel();

    /** Show github panel. */
    void showGithubPanel();

    /**
     * Set the visibility state of the loader.
     *
     * @param isVisible
     *         <code>true</code> if visible.
     */
    void setLoaderVisibility(boolean isVisible);

}