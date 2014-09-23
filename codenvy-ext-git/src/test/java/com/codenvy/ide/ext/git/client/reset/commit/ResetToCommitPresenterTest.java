import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.api.event.FileEvent;
import com.codenvy.ide.api.projecttree.generic.FileNode;
    private FileNode               file;
        presenter = new ResetToCommitPresenter(view, service, constant, eventBus, editorAgent, appContext, notificationManager,
        when(selectedRevision.getId()).thenReturn(PROJECT_PATH);
        }).when(service).log((ProjectDescriptor)anyObject(), anyBoolean(), (AsyncRequestCallback<LogResponse>)anyObject());
        verify(appContext).getCurrentProject();
        verify(service).log(eq(rootProjectDescriptor), eq(!IS_TEXT_FORMATTED), (AsyncRequestCallback<LogResponse>)anyObject());
        }).when(service).log((ProjectDescriptor)anyObject(), anyBoolean(), (AsyncRequestCallback<LogResponse>)anyObject());
        verify(appContext).getCurrentProject();
        verify(service).log(eq(rootProjectDescriptor), eq(!IS_TEXT_FORMATTED), (AsyncRequestCallback<LogResponse>)anyObject());
//    @Test
//    public void testOnResetClickedWhenResetTypeNotEqualsHardOrMergeAndDiffAndResetRequestsIsSuccessful() throws Exception {
//        when(view.isSoftMode()).thenReturn(true);
//        doAnswer(new Answer() {
//            @Override
//            public Object answer(InvocationOnMock invocation) throws Throwable {
//                Object[] arguments = invocation.getArguments();
//                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[7];
//                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
//                onSuccess.invoke(callback, DIFF_WITH_NEW_FILE);
//                return callback;
//            }
//        }).when(service).diff(eq(rootProjectDescriptor), anyList(), eq(DiffRequest.DiffType.RAW), eq(true), eq(0), eq(PROJECT_PATH), eq(false),
//                              (AsyncRequestCallback<String>)anyObject());
//
//        doAnswer(new Answer() {
//            @Override
//            public Object answer(InvocationOnMock invocation) throws Throwable {
//                Object[] arguments = invocation.getArguments();
//                AsyncRequestCallback<Void> callback = (AsyncRequestCallback<Void>)arguments[3];
//                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
//                onSuccess.invoke(callback, (Void)null);
//                return callback;
//            }
//        }).when(service)
//          .reset((ProjectDescriptor)anyObject(), anyString(), (ResetRequest.ResetType)anyObject(), (AsyncRequestCallback<Void>)anyObject());
//
//        presenter.onRevisionSelected(selectedRevision);
//        presenter.onResetClicked();
//
//        verify(view).close();
//        verify(selectedRevision, times(2)).getId();
//        verify(appContext, times(2)).getCurrentProject();
//        verify(service).diff(eq(rootProjectDescriptor), anyList(), eq(DiffRequest.DiffType.RAW), eq(true), eq(0), eq(PROJECT_PATH), eq(false),
//                             (AsyncRequestCallback<String>)anyObject());
//        verify(service).reset((ProjectDescriptor)anyObject(), eq(PROJECT_PATH), eq(MIXED), (AsyncRequestCallback<Void>)anyObject());
//        verify(notificationManager).showNotification((Notification)anyObject());
//    }
            throws Exception {
        }).when(service).diff(eq(rootProjectDescriptor), anyList(), eq(DiffRequest.DiffType.RAW), eq(true), eq(0), eq(PROJECT_PATH), eq(false),
        }).when(service)
          .reset((ProjectDescriptor)anyObject(), anyString(), (ResetRequest.ResetType)anyObject(), (AsyncRequestCallback<Void>)anyObject());
        verify(appContext, times(3)).getCurrentProject();
        verify(service).diff(eq(rootProjectDescriptor), anyList(), eq(DiffRequest.DiffType.RAW), eq(true), eq(0), eq(PROJECT_PATH), eq(false),
        verify(service).reset((ProjectDescriptor)anyObject(), eq(PROJECT_PATH), eq(HARD), (AsyncRequestCallback<Void>)anyObject());
        verify(eventBus, times(2)).fireEvent((FileEvent)anyObject());
//    @Test
//    public void testOnResetClickedWhenResetTypeEqualsHardOrMergeAndWhenFileIsChangedInCommitToResetAndDiffAndResetRequestsIsSuccessful()
//            throws Exception {
//        when(view.isMixMode()).thenReturn(false);
////        when(view.isMergeMode()).thenReturn(true);
//
//        doAnswer(new Answer() {
//            @Override
//            public Object answer(InvocationOnMock invocation) throws Throwable {
//                Object[] arguments = invocation.getArguments();
//                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[7];
//                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
//                onSuccess.invoke(callback, DIFF_FILE_CHANGED);
//                return callback;
//            }
//        }).when(service).diff(eq(rootProjectDescriptor), anyList(), eq(DiffRequest.DiffType.RAW), eq(true), eq(0), eq(PROJECT_PATH), eq(false),
//                              (AsyncRequestCallback<String>)anyObject());
//
//        doAnswer(new Answer() {
//            @Override
//            public Object answer(InvocationOnMock invocation) throws Throwable {
//                Object[] arguments = invocation.getArguments();
//                AsyncRequestCallback<Void> callback = (AsyncRequestCallback<Void>)arguments[3];
//                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
//                onSuccess.invoke(callback, (Void)null);
//                return callback;
//            }
//        }).when(service)
//          .reset((ProjectDescriptor)anyObject(), anyString(), (ResetRequest.ResetType)anyObject(), (AsyncRequestCallback<Void>)anyObject());
//
//        presenter.onRevisionSelected(selectedRevision);
//        presenter.onResetClicked();
//
//        verify(view).close();
//        verify(selectedRevision, times(2)).getId();
//        verify(appContext, times(3)).getCurrentProject();
//        verify(service).diff(eq(rootProjectDescriptor), anyList(), eq(DiffRequest.DiffType.RAW), eq(true), eq(0), eq(PROJECT_PATH), eq(false),
//                             (AsyncRequestCallback<String>)anyObject());
//        verify(service).reset((ProjectDescriptor)anyObject(), eq(PROJECT_PATH), eq(MERGE), (AsyncRequestCallback<Void>)anyObject());
//        verify(partPresenter).init(eq(editorInput));
//        verify(notificationManager).showNotification((Notification)anyObject());
//    }
        }).when(service).diff(eq(projectDescriptor), anyList(), eq(DiffRequest.DiffType.RAW), eq(true), eq(0), eq(PROJECT_PATH), eq(false),
        verify(appContext).getCurrentProject();
        verify(service).diff(eq(rootProjectDescriptor), anyList(), eq(DiffRequest.DiffType.RAW), eq(true), eq(0), eq(PROJECT_PATH), eq(false),
        verify(service, never()).reset((ProjectDescriptor)anyObject(), anyString(), (ResetRequest.ResetType)anyObject(),
        }).when(service).diff(eq(rootProjectDescriptor), anyList(), eq(DiffRequest.DiffType.RAW), eq(true), eq(0), eq(PROJECT_PATH), eq(false),
        }).when(service).reset((ProjectDescriptor)anyObject(), anyString(), (ResetRequest.ResetType)anyObject(),
        verify(appContext, times(2)).getCurrentProject();
        verify(service).diff(eq(rootProjectDescriptor), anyList(), eq(DiffRequest.DiffType.RAW), eq(true), eq(0), eq(PROJECT_PATH), eq(false),
        verify(service).reset((ProjectDescriptor)anyObject(), eq(PROJECT_PATH), eq(MIXED), (AsyncRequestCallback<Void>)anyObject());