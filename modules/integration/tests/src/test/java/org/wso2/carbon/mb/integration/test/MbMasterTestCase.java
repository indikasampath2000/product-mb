package org.wso2.carbon.mb.integration.test;

/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.core.utils.environmentutils.EnvironmentBuilder;
import org.wso2.carbon.automation.core.utils.environmentutils.EnvironmentVariables;

import java.rmi.RemoteException;

public class MbMasterTestCase {

    private EnvironmentVariables mbEnvironment;

    public void init(int userId) throws RemoteException, LoginAuthenticationExceptionException {
        EnvironmentBuilder builder = new EnvironmentBuilder().mb(userId);
        mbEnvironment = builder.build().getMb();
    }

    public EnvironmentVariables getEnvironment() {
        return mbEnvironment;
    }
}
