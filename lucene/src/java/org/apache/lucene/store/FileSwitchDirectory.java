begin_unit
begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
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
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import
begin_comment
comment|/**  * Expert: A Directory instance that switches files between  * two other Directory instances.   *<p>Files with the specified extensions are placed in the  * primary directory; others are placed in the secondary  * directory.  The provided Set must not change once passed  * to this class, and must allow multiple threads to call  * contains at once.</p>  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|FileSwitchDirectory
specifier|public
class|class
name|FileSwitchDirectory
extends|extends
name|Directory
block|{
DECL|field|secondaryDir
specifier|private
specifier|final
name|Directory
name|secondaryDir
decl_stmt|;
DECL|field|primaryDir
specifier|private
specifier|final
name|Directory
name|primaryDir
decl_stmt|;
DECL|field|primaryExtensions
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|primaryExtensions
decl_stmt|;
DECL|field|doClose
specifier|private
name|boolean
name|doClose
decl_stmt|;
DECL|method|FileSwitchDirectory
specifier|public
name|FileSwitchDirectory
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|primaryExtensions
parameter_list|,
name|Directory
name|primaryDir
parameter_list|,
name|Directory
name|secondaryDir
parameter_list|,
name|boolean
name|doClose
parameter_list|)
block|{
name|this
operator|.
name|primaryExtensions
operator|=
name|primaryExtensions
expr_stmt|;
name|this
operator|.
name|primaryDir
operator|=
name|primaryDir
expr_stmt|;
name|this
operator|.
name|secondaryDir
operator|=
name|secondaryDir
expr_stmt|;
name|this
operator|.
name|doClose
operator|=
name|doClose
expr_stmt|;
name|this
operator|.
name|lockFactory
operator|=
name|primaryDir
operator|.
name|getLockFactory
argument_list|()
expr_stmt|;
block|}
comment|/** Return the primary directory */
DECL|method|getPrimaryDir
specifier|public
name|Directory
name|getPrimaryDir
parameter_list|()
block|{
return|return
name|primaryDir
return|;
block|}
comment|/** Return the secondary directory */
DECL|method|getSecondaryDir
specifier|public
name|Directory
name|getSecondaryDir
parameter_list|()
block|{
return|return
name|secondaryDir
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|doClose
condition|)
block|{
try|try
block|{
name|secondaryDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|primaryDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|doClose
operator|=
literal|false
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|listAll
specifier|public
name|String
index|[]
name|listAll
parameter_list|()
throws|throws
name|IOException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|files
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|f
range|:
name|primaryDir
operator|.
name|listAll
argument_list|()
control|)
block|{
name|files
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|f
range|:
name|secondaryDir
operator|.
name|listAll
argument_list|()
control|)
block|{
name|files
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
return|return
name|files
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|files
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|/** Utility method to return a file's extension. */
DECL|method|getExtension
specifier|public
specifier|static
name|String
name|getExtension
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|int
name|i
init|=
name|name
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|==
operator|-
literal|1
condition|)
block|{
return|return
literal|""
return|;
block|}
return|return
name|name
operator|.
name|substring
argument_list|(
name|i
operator|+
literal|1
argument_list|,
name|name
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getDirectory
specifier|private
name|Directory
name|getDirectory
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|String
name|ext
init|=
name|getExtension
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|primaryExtensions
operator|.
name|contains
argument_list|(
name|ext
argument_list|)
condition|)
block|{
return|return
name|primaryDir
return|;
block|}
else|else
block|{
return|return
name|secondaryDir
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|fileExists
specifier|public
name|boolean
name|fileExists
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getDirectory
argument_list|(
name|name
argument_list|)
operator|.
name|fileExists
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|fileModified
specifier|public
name|long
name|fileModified
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getDirectory
argument_list|(
name|name
argument_list|)
operator|.
name|fileModified
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|deleteFile
specifier|public
name|void
name|deleteFile
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|getDirectory
argument_list|(
name|name
argument_list|)
operator|.
name|deleteFile
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fileLength
specifier|public
name|long
name|fileLength
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getDirectory
argument_list|(
name|name
argument_list|)
operator|.
name|fileLength
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createOutput
specifier|public
name|IndexOutput
name|createOutput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getDirectory
argument_list|(
name|name
argument_list|)
operator|.
name|createOutput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|sync
specifier|public
name|void
name|sync
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|primaryNames
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|secondaryNames
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|names
control|)
if|if
condition|(
name|primaryExtensions
operator|.
name|contains
argument_list|(
name|getExtension
argument_list|(
name|name
argument_list|)
argument_list|)
condition|)
name|primaryNames
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
else|else
name|secondaryNames
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|primaryDir
operator|.
name|sync
argument_list|(
name|primaryNames
argument_list|)
expr_stmt|;
name|secondaryDir
operator|.
name|sync
argument_list|(
name|secondaryNames
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|openInput
specifier|public
name|IndexInput
name|openInput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getDirectory
argument_list|(
name|name
argument_list|)
operator|.
name|openInput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
return|;
block|}
block|}
end_class
end_unit
