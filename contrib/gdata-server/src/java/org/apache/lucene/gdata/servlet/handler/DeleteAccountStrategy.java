begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.gdata.servlet.handler
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|servlet
operator|.
name|handler
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|data
operator|.
name|GDataAccount
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|server
operator|.
name|ServiceException
import|;
end_import
begin_comment
comment|/**  * @author Simon Willnauer  *  */
end_comment
begin_class
DECL|class|DeleteAccountStrategy
specifier|public
class|class
name|DeleteAccountStrategy
extends|extends
name|AbstractAccountHandler
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|DefaultDeleteHandler
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|processServiceAction
specifier|protected
name|void
name|processServiceAction
parameter_list|(
name|GDataAccount
name|account
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
name|this
operator|.
name|service
operator|.
name|deleteAccount
argument_list|(
name|account
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Can't delete account -- "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
block|}
end_class
end_unit
