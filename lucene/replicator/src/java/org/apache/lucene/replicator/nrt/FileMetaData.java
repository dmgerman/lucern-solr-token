begin_unit
begin_package
DECL|package|org.apache.lucene.replicator.nrt
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|replicator
operator|.
name|nrt
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/** Holds metadata details about a single file that we use to confirm two files (one remote, one local) are in fact "identical".  *  * @lucene.experimental */
end_comment
begin_class
DECL|class|FileMetaData
specifier|public
class|class
name|FileMetaData
block|{
comment|// Header and footer of the file must be identical between primary and replica to consider the files equal:
DECL|field|header
specifier|public
specifier|final
name|byte
index|[]
name|header
decl_stmt|;
DECL|field|footer
specifier|public
specifier|final
name|byte
index|[]
name|footer
decl_stmt|;
DECL|field|length
specifier|public
specifier|final
name|long
name|length
decl_stmt|;
comment|// Used to ensure no bit flips when copying the file:
DECL|field|checksum
specifier|public
specifier|final
name|long
name|checksum
decl_stmt|;
DECL|method|FileMetaData
specifier|public
name|FileMetaData
parameter_list|(
name|byte
index|[]
name|header
parameter_list|,
name|byte
index|[]
name|footer
parameter_list|,
name|long
name|length
parameter_list|,
name|long
name|checksum
parameter_list|)
block|{
name|this
operator|.
name|header
operator|=
name|header
expr_stmt|;
name|this
operator|.
name|footer
operator|=
name|footer
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
name|this
operator|.
name|checksum
operator|=
name|checksum
expr_stmt|;
block|}
block|}
end_class
end_unit
