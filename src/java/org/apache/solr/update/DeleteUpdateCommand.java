begin_unit
begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
package|;
end_package
begin_comment
comment|/**  * @author yonik  * @version $Id$  */
end_comment
begin_class
DECL|class|DeleteUpdateCommand
specifier|public
class|class
name|DeleteUpdateCommand
extends|extends
name|UpdateCommand
block|{
DECL|field|id
specifier|public
name|String
name|id
decl_stmt|;
DECL|field|query
specifier|public
name|String
name|query
decl_stmt|;
DECL|field|fromPending
specifier|public
name|boolean
name|fromPending
decl_stmt|;
DECL|field|fromCommitted
specifier|public
name|boolean
name|fromCommitted
decl_stmt|;
DECL|method|DeleteUpdateCommand
specifier|public
name|DeleteUpdateCommand
parameter_list|()
block|{
name|super
argument_list|(
literal|"delete"
argument_list|)
expr_stmt|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|commandName
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
if|if
condition|(
name|id
operator|!=
literal|null
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|"id="
argument_list|)
operator|.
name|append
argument_list|(
name|id
argument_list|)
expr_stmt|;
else|else
name|sb
operator|.
name|append
argument_list|(
literal|"query=`"
argument_list|)
operator|.
name|append
argument_list|(
name|query
argument_list|)
operator|.
name|append
argument_list|(
literal|'`'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",fromPending="
argument_list|)
operator|.
name|append
argument_list|(
name|fromPending
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",fromCommitted="
argument_list|)
operator|.
name|append
argument_list|(
name|fromCommitted
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
