begin_unit
begin_package
DECL|package|org.apache.regexp
package|package
name|org
operator|.
name|apache
operator|.
name|regexp
package|;
end_package
begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * This class exists as a gateway to access useful Jakarta Regexp package protected data.  */
end_comment
begin_class
DECL|class|RegexpTunnel
specifier|public
class|class
name|RegexpTunnel
block|{
DECL|method|getPrefix
specifier|public
specifier|static
name|char
index|[]
name|getPrefix
parameter_list|(
name|RE
name|regexp
parameter_list|)
block|{
name|REProgram
name|program
init|=
name|regexp
operator|.
name|getProgram
argument_list|()
decl_stmt|;
return|return
name|program
operator|.
name|prefix
return|;
block|}
block|}
end_class
end_unit
