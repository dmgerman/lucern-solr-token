begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.idversion
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|idversion
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|BlockTermState
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
name|TermState
import|;
end_import
begin_class
DECL|class|IDVersionTermState
specifier|final
class|class
name|IDVersionTermState
extends|extends
name|BlockTermState
block|{
DECL|field|idVersion
name|long
name|idVersion
decl_stmt|;
DECL|field|docID
name|int
name|docID
decl_stmt|;
annotation|@
name|Override
DECL|method|clone
specifier|public
name|IDVersionTermState
name|clone
parameter_list|()
block|{
name|IDVersionTermState
name|other
init|=
operator|new
name|IDVersionTermState
argument_list|()
decl_stmt|;
name|other
operator|.
name|copyFrom
argument_list|(
name|this
argument_list|)
expr_stmt|;
return|return
name|other
return|;
block|}
annotation|@
name|Override
DECL|method|copyFrom
specifier|public
name|void
name|copyFrom
parameter_list|(
name|TermState
name|_other
parameter_list|)
block|{
name|super
operator|.
name|copyFrom
argument_list|(
name|_other
argument_list|)
expr_stmt|;
name|IDVersionTermState
name|other
init|=
operator|(
name|IDVersionTermState
operator|)
name|_other
decl_stmt|;
name|idVersion
operator|=
name|other
operator|.
name|idVersion
expr_stmt|;
name|docID
operator|=
name|other
operator|.
name|docID
expr_stmt|;
block|}
block|}
end_class
end_unit