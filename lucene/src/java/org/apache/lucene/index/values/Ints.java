begin_unit
begin_package
DECL|package|org.apache.lucene.index.values
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|values
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
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
name|index
operator|.
name|values
operator|.
name|IntsImpl
operator|.
name|IntsReader
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
name|index
operator|.
name|values
operator|.
name|IntsImpl
operator|.
name|IntsWriter
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
name|store
operator|.
name|Directory
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
name|store
operator|.
name|IOContext
import|;
end_import
begin_comment
comment|/**  * @lucene.experimental  */
end_comment
begin_class
DECL|class|Ints
specifier|public
class|class
name|Ints
block|{
comment|// TODO - add bulk copy where possible
DECL|method|Ints
specifier|private
name|Ints
parameter_list|()
block|{   }
DECL|method|getWriter
specifier|public
specifier|static
name|Writer
name|getWriter
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|boolean
name|useFixedArray
parameter_list|,
name|AtomicLong
name|bytesUsed
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO - implement fixed?!
return|return
operator|new
name|IntsWriter
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|bytesUsed
argument_list|,
name|context
argument_list|)
return|;
block|}
DECL|method|getValues
specifier|public
specifier|static
name|IndexDocValues
name|getValues
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|boolean
name|useFixedArray
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|IntsReader
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|context
argument_list|)
return|;
block|}
block|}
end_class
end_unit
