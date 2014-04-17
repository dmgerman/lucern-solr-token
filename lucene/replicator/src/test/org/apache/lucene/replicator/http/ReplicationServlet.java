begin_unit
begin_package
DECL|package|org.apache.lucene.replicator.http
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|replicator
operator|.
name|http
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletException
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServlet
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
import|;
end_import
begin_class
DECL|class|ReplicationServlet
specifier|public
class|class
name|ReplicationServlet
extends|extends
name|HttpServlet
block|{
DECL|field|service
specifier|private
specifier|final
name|ReplicationService
name|service
decl_stmt|;
DECL|field|respondWithError
specifier|private
name|boolean
name|respondWithError
init|=
literal|false
decl_stmt|;
DECL|method|ReplicationServlet
specifier|public
name|ReplicationServlet
parameter_list|(
name|ReplicationService
name|service
parameter_list|)
block|{
name|this
operator|.
name|service
operator|=
name|service
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doGet
specifier|protected
name|void
name|doGet
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|resp
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
if|if
condition|(
name|respondWithError
condition|)
block|{
name|resp
operator|.
name|sendError
argument_list|(
literal|500
argument_list|,
literal|"Fake error"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|service
operator|.
name|perform
argument_list|(
name|req
argument_list|,
name|resp
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setRespondWithError
specifier|public
name|void
name|setRespondWithError
parameter_list|(
name|boolean
name|respondWithError
parameter_list|)
block|{
name|this
operator|.
name|respondWithError
operator|=
name|respondWithError
expr_stmt|;
block|}
block|}
end_class
end_unit
