begin_unit
begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ThreadInfo
import|;
end_import
begin_class
DECL|class|Diagnostics
specifier|public
class|class
name|Diagnostics
block|{
DECL|field|log
specifier|protected
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|Diagnostics
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|logThreadDumps
specifier|public
specifier|static
name|void
name|logThreadDumps
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|32768
argument_list|)
decl_stmt|;
if|if
condition|(
name|message
operator|==
literal|null
condition|)
name|message
operator|=
literal|"============ THREAD DUMP REQUESTED ============"
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|ThreadInfo
index|[]
name|threads
init|=
name|ManagementFactory
operator|.
name|getThreadMXBean
argument_list|()
operator|.
name|dumpAllThreads
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
for|for
control|(
name|ThreadInfo
name|info
range|:
name|threads
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|info
argument_list|)
expr_stmt|;
comment|// sb.append("\n");
block|}
name|log
operator|.
name|error
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
