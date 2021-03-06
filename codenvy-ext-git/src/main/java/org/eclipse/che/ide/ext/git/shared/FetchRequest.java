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
package org.eclipse.che.ide.ext.git.shared;

import org.eclipse.che.dto.shared.DTO;

import java.util.List;

/**
 * Request to fetch data from remote repository.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: FetchRequest.java 22817 2011-03-22 09:17:52Z andrew00x $
 */
@DTO
public interface FetchRequest extends GitRequest {
    /** @return list of refspec to fetch */
    List<String> getRefSpec();
    
    void setRefSpec(List<String> refSpec);
    
    FetchRequest withRefSpec(List<String> refSpec);

    /** @return remote name. If <code>null</code> then 'origin' will be used */
    String getRemote();
    
    void setRemote(String remote);
    
    FetchRequest withRemote(String remote);

    /** @return <code>true</code> if local refs must be deleted if they deleted in remote repository and <code>false</code> otherwise */
    boolean isRemoveDeletedRefs();
    
    void setRemoveDeletedRefs(boolean isRemoveDeletedRefs);
    
    FetchRequest withRemoveDeletedRefs(boolean isRemoveDeletedRefs);

    /** @return time (in seconds) to wait without data transfer occurring before aborting fetching data from remote repository */
    int getTimeout();
    
    void setTimeout(int timeout);
    
    FetchRequest withTimeout(int timeout);
}