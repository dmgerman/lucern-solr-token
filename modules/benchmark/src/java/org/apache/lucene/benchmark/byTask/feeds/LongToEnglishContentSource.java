begin_unit
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.feeds
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import
begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|RuleBasedNumberFormat
import|;
end_import
begin_comment
comment|/**  * Creates documents whose content is a<code>long</code> number starting from  *<code>{@link Long#MIN_VALUE} + 10</code>.  */
end_comment
begin_class
DECL|class|LongToEnglishContentSource
specifier|public
class|class
name|LongToEnglishContentSource
extends|extends
name|ContentSource
block|{
DECL|field|counter
specifier|private
name|long
name|counter
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{   }
comment|// TODO: we could take param to specify locale...
DECL|field|rnbf
specifier|private
specifier|final
name|RuleBasedNumberFormat
name|rnbf
init|=
operator|new
name|RuleBasedNumberFormat
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|,
name|RuleBasedNumberFormat
operator|.
name|SPELLOUT
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|getNextDocData
specifier|public
specifier|synchronized
name|DocData
name|getNextDocData
parameter_list|(
name|DocData
name|docData
parameter_list|)
throws|throws
name|NoMoreDataException
throws|,
name|IOException
block|{
name|docData
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// store the current counter to avoid synchronization later on
name|long
name|curCounter
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|curCounter
operator|=
name|counter
expr_stmt|;
if|if
condition|(
name|counter
operator|==
name|Long
operator|.
name|MAX_VALUE
condition|)
block|{
name|counter
operator|=
name|Long
operator|.
name|MIN_VALUE
expr_stmt|;
comment|//loop around
block|}
else|else
block|{
operator|++
name|counter
expr_stmt|;
block|}
block|}
name|docData
operator|.
name|setBody
argument_list|(
name|rnbf
operator|.
name|format
argument_list|(
name|curCounter
argument_list|)
argument_list|)
expr_stmt|;
name|docData
operator|.
name|setName
argument_list|(
literal|"doc_"
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|curCounter
argument_list|)
argument_list|)
expr_stmt|;
name|docData
operator|.
name|setTitle
argument_list|(
literal|"title_"
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|curCounter
argument_list|)
argument_list|)
expr_stmt|;
name|docData
operator|.
name|setDate
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|docData
return|;
block|}
annotation|@
name|Override
DECL|method|resetInputs
specifier|public
name|void
name|resetInputs
parameter_list|()
throws|throws
name|IOException
block|{
name|counter
operator|=
name|Long
operator|.
name|MIN_VALUE
operator|+
literal|10
expr_stmt|;
block|}
block|}
end_class
end_unit
