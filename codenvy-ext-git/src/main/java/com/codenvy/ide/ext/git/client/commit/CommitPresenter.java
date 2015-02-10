/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.git.client.commit;

import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.projecttree.generic.StorableNode;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.GitServiceClient;
import com.codenvy.ide.ext.git.client.DateTimeFormatter;
import com.codenvy.ide.ext.git.shared.LogResponse;
import com.codenvy.ide.ext.git.shared.Revision;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;

/**
 * Presenter for commit changes on git.
 *
 * @author Ann Zhuleva
 */
@Singleton
public class CommitPresenter implements CommitView.ActionDelegate {
    private final DtoUnmarshallerFactory  dtoUnmarshallerFactory;
    private final AppContext              appContext;
    private final CommitView              view;
    private final GitServiceClient        service;
    private final GitLocalizationConstant constant;
    private final NotificationManager     notificationManager;
    private final DateTimeFormatter       dateTimeFormatter;
    private final SelectionAgent          selectionAgent;

    @Inject
    public CommitPresenter(CommitView view,
                           GitServiceClient service,
                           GitLocalizationConstant constant,
                           NotificationManager notificationManager,
                           DtoUnmarshallerFactory dtoUnmarshallerFactory,
                           AppContext appContext,
                           DateTimeFormatter dateTimeFormatter,
                           final SelectionAgent selectionAgent) {
        this.view = view;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.appContext = appContext;
        this.dateTimeFormatter = dateTimeFormatter;
        this.view.setDelegate(this);
        this.service = service;
        this.constant = constant;
        this.notificationManager = notificationManager;
        this.selectionAgent = selectionAgent;
    }

    /** Show dialog. */
    public void showDialog() {
        view.setAmend(false);
        view.setAllFilesInclude(false);
        view.setIncludeSelection(false);
        view.setOnlySelection(false);
        view.setEnableCommitButton(!view.getMessage().isEmpty());
        view.showDialog();
        view.focusInMessageField();
    }

    /** {@inheritDoc} */
    @Override
    public void onCommitClicked() {
        final String message = view.getMessage();
        final boolean all = view.isAllFilesInclued();
        final boolean amend = view.isAmend();
        final boolean selectionFlag = this.view.isIncludeSelection();
        final boolean onlySelectionFlag = this.view.isOnlySelection();

        if (selectionFlag) {
            commitAddSelection(message, amend);
        } else if (onlySelectionFlag) {
            commitOnlySelection(message, amend);
        } else {
            doCommit(message, all, amend);
        }

    }

    private void commitAddSelection(final String message, final boolean amend) {
        // first git-add the selection
        @SuppressWarnings("unchecked")
        final Selection<StorableNode< ? >> selection = (Selection<StorableNode< ? >>)this.selectionAgent.getSelection();
        if (selection.isEmpty() || !(selection.getFirstElement() instanceof StorableNode)) {
            doCommit(message, false, amend);
        } else {
            final List<String> filePattern = buildFileList(selection);

            try {
                service.add(appContext.getCurrentProject().getRootProject(), false, filePattern, new RequestCallback<Void>() {
                    @Override
                    protected void onSuccess(final Void result) {
                        // then commit
                        doCommit(message, false, amend);
                    }
                    @Override
                    protected void onFailure(final Throwable exception) {
                        handleError(exception);
                    }
                });
            } catch (final WebSocketException e) {
                handleError(new Exception("Communication error with the server", e));
            }
        }
    }

    private List<String> buildFileList(final Selection<StorableNode< ? >> selection) {
        final List<String> filePattern = new ArrayList<>();
        final Array<StorableNode< ? >> selected = selection.getAll();
        final String base = appContext.getCurrentProject().getRootProject().getPath();
        for (final StorableNode< ? > node : selected.asIterable()) {
            filePattern.add(getPath(node, base));
        }
        return filePattern;
    }

    private void commitOnlySelection(final String message, final boolean amend) {
        // first git-add the selection
        @SuppressWarnings("unchecked")
        final Selection<StorableNode< ? >> selection = (Selection<StorableNode< ? >>)this.selectionAgent.getSelection();
        if (!selection.isEmpty() && (selection.getFirstElement() instanceof StorableNode)) {
            final List<String> files = buildFileList(selection);
            service.commit(appContext.getCurrentProject().getRootProject(), message, files, amend,
                           new AsyncRequestCallback<Revision>(dtoUnmarshallerFactory.newUnmarshaller(Revision.class)) {
                               @Override
                               protected void onSuccess(final Revision result) {
                                   if (!result.isFake()) {
                                       onCommitSuccess(result);
                                   } else {
                                       final Notification notification = new Notification(result.getMessage(), ERROR);
                                       notificationManager.showNotification(notification);
                                   }
                               }

                               @Override
                               protected void onFailure(final Throwable exception) {
                                   handleError(exception);
                               }
                           }
                   );
        } // else don't commit as it goes against user intent
        this.view.close();
    }

    private void doCommit(final String message, final boolean all, final boolean amend) {
        service.commit(appContext.getCurrentProject().getRootProject(), message, all, amend,
                       new AsyncRequestCallback<Revision>(dtoUnmarshallerFactory.newUnmarshaller(Revision.class)) {
                           @Override
                           protected void onSuccess(final Revision result) {
                               if (!result.isFake()) {
                                   onCommitSuccess(result);
                               } else {
                                   final Notification notification = new Notification(result.getMessage(), ERROR);
                                   notificationManager.showNotification(notification);
                               }
                           }

                           @Override
                           protected void onFailure(final Throwable exception) {
                               handleError(exception);
                           }
                       }
               );
        this.view.close();
    }

    private String getPath(final StorableNode< ? > node, final String base) {
        String path = node.getPath();
        if (path.startsWith(base)) {
            path = path.replaceFirst(base, "");
        }
        if (path.startsWith("/")) {
            path = path.replaceFirst("/", "");
        }
        if (path.isEmpty() || "/".equals(path)) {
            path = ".";
        }
        return path;
    }

    /**
     * Performs action when commit is successfully completed.
     *
     * @param revision
     *         a {@link Revision}
     */
    private void onCommitSuccess(@Nonnull final Revision revision) {
        String date = dateTimeFormatter.getFormattedDate(revision.getCommitTime());
        String message = constant.commitMessage(revision.getId(), date);

        if ((revision.getCommitter() != null && revision.getCommitter().getName() != null &&
             !revision.getCommitter().getName().isEmpty())) {
            message += " " + constant.commitUser(revision.getCommitter().getName());
        }

        Notification notification = new Notification(message, INFO);
        notificationManager.showNotification(notification);
        view.setMessage("");
    }

    /**
     * Handler some action whether some exception happened.
     *
     * @param e
     *         exception what happened
     */
    private void handleError(@Nonnull Throwable e) {
        String errorMessage = (e.getMessage() != null && !e.getMessage().isEmpty()) ? e.getMessage() : constant.commitFailed();
        Notification notification = new Notification(errorMessage, ERROR);
        notificationManager.showNotification(notification);
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onValueChanged() {
        String message = view.getMessage();
        view.setEnableCommitButton(!message.isEmpty());
    }

    @Override
    public void setAmendCommitMessage() {
        final Unmarshallable<LogResponse> unmarshall = dtoUnmarshallerFactory.newUnmarshaller(LogResponse.class);
        this.service.log(appContext.getCurrentProject().getRootProject(), false, new AsyncRequestCallback<LogResponse>(unmarshall) {
            @Override
            protected void onSuccess(final LogResponse result) {
                final List<Revision> commits = result.getCommits();
                String message = "";
                if (commits != null && (!commits.isEmpty())) {
                    final Revision tip = commits.get(0);
                    if (tip != null) {
                        message = tip.getMessage();
                        if (message != null) {
                            view.setMessage(message);
                        }
                    }
                }
                CommitPresenter.this.view.setMessage(message);
            }

            @Override
            protected void onFailure(final Throwable exception) {
                Log.warn(CommitPresenter.class, "Git log failed", exception);
                CommitPresenter.this.view.setMessage("");
            }
        });
    }
}