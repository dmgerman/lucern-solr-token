begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
import|;
end_import
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
name|io
operator|.
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DecimalFormat
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
name|List
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
name|codecs
operator|.
name|Codec
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
name|codecs
operator|.
name|CodecProvider
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
name|FSDirectory
import|;
end_import
begin_comment
comment|/**  * Command-line tool that enables listing segments in an  * index, copying specific segments to another index, and  * deleting segments from an index.  *  *<p>This tool does file-level copying of segments files.  * This means it's unable to split apart a single segment  * into multiple segments.  For example if your index is  * optimized, this tool won't help.  Also, it does basic  * file-level copying (using simple  * File{In,Out}putStream) so it will not work with non  * FSDirectory Directory impls.</p>  *  * @lucene.experimental You can easily  * accidentally remove segments from your index so be  * careful!  */
end_comment
begin_class
DECL|class|IndexSplitter
specifier|public
class|class
name|IndexSplitter
block|{
DECL|field|infos
specifier|public
name|SegmentInfos
name|infos
decl_stmt|;
DECL|field|codecs
specifier|private
specifier|final
name|CodecProvider
name|codecs
decl_stmt|;
DECL|field|fsDir
name|FSDirectory
name|fsDir
decl_stmt|;
DECL|field|dir
name|File
name|dir
decl_stmt|;
comment|/**    * @param args    */
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|<
literal|2
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: IndexSplitter<srcDir> -l (list the segments and their sizes)"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"IndexSplitter<srcDir><destDir><segments>+"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"IndexSplitter<srcDir> -d (delete the following segments)"
argument_list|)
expr_stmt|;
return|return;
block|}
name|File
name|srcDir
init|=
operator|new
name|File
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|IndexSplitter
name|is
init|=
operator|new
name|IndexSplitter
argument_list|(
name|srcDir
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|srcDir
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"srcdir:"
operator|+
name|srcDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" doesn't exist"
argument_list|)
throw|;
block|}
if|if
condition|(
name|args
index|[
literal|1
index|]
operator|.
name|equals
argument_list|(
literal|"-l"
argument_list|)
condition|)
block|{
name|is
operator|.
name|listSegments
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
literal|1
index|]
operator|.
name|equals
argument_list|(
literal|"-d"
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|segs
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
name|int
name|x
init|=
literal|2
init|;
name|x
operator|<
name|args
operator|.
name|length
condition|;
name|x
operator|++
control|)
block|{
name|segs
operator|.
name|add
argument_list|(
name|args
index|[
name|x
index|]
argument_list|)
expr_stmt|;
block|}
name|is
operator|.
name|remove
argument_list|(
name|segs
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|File
name|targetDir
init|=
operator|new
name|File
argument_list|(
name|args
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|segs
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
name|int
name|x
init|=
literal|2
init|;
name|x
operator|<
name|args
operator|.
name|length
condition|;
name|x
operator|++
control|)
block|{
name|segs
operator|.
name|add
argument_list|(
name|args
index|[
name|x
index|]
argument_list|)
expr_stmt|;
block|}
name|is
operator|.
name|split
argument_list|(
name|targetDir
argument_list|,
name|segs
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|IndexSplitter
specifier|public
name|IndexSplitter
parameter_list|(
name|File
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|dir
argument_list|,
name|CodecProvider
operator|.
name|getDefault
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|IndexSplitter
specifier|public
name|IndexSplitter
parameter_list|(
name|File
name|dir
parameter_list|,
name|CodecProvider
name|codecs
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
name|this
operator|.
name|codecs
operator|=
name|codecs
expr_stmt|;
name|fsDir
operator|=
name|FSDirectory
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|infos
operator|=
operator|new
name|SegmentInfos
argument_list|(
name|codecs
argument_list|)
expr_stmt|;
name|infos
operator|.
name|read
argument_list|(
name|fsDir
argument_list|,
name|codecs
argument_list|)
expr_stmt|;
block|}
DECL|method|listSegments
specifier|public
name|void
name|listSegments
parameter_list|()
throws|throws
name|IOException
block|{
name|DecimalFormat
name|formatter
init|=
operator|new
name|DecimalFormat
argument_list|(
literal|"###,###.###"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|infos
operator|.
name|size
argument_list|()
condition|;
name|x
operator|++
control|)
block|{
name|SegmentInfo
name|info
init|=
name|infos
operator|.
name|info
argument_list|(
name|x
argument_list|)
decl_stmt|;
name|String
name|sizeStr
init|=
name|formatter
operator|.
name|format
argument_list|(
name|info
operator|.
name|sizeInBytes
argument_list|()
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|info
operator|.
name|name
operator|+
literal|" "
operator|+
name|sizeStr
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getIdx
specifier|private
name|int
name|getIdx
parameter_list|(
name|String
name|name
parameter_list|)
block|{
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|infos
operator|.
name|size
argument_list|()
condition|;
name|x
operator|++
control|)
block|{
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|infos
operator|.
name|info
argument_list|(
name|x
argument_list|)
operator|.
name|name
argument_list|)
condition|)
return|return
name|x
return|;
block|}
return|return
operator|-
literal|1
return|;
block|}
DECL|method|getInfo
specifier|private
name|SegmentInfo
name|getInfo
parameter_list|(
name|String
name|name
parameter_list|)
block|{
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|infos
operator|.
name|size
argument_list|()
condition|;
name|x
operator|++
control|)
block|{
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|infos
operator|.
name|info
argument_list|(
name|x
argument_list|)
operator|.
name|name
argument_list|)
condition|)
return|return
name|infos
operator|.
name|info
argument_list|(
name|x
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|(
name|String
index|[]
name|segs
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|String
name|n
range|:
name|segs
control|)
block|{
name|int
name|idx
init|=
name|getIdx
argument_list|(
name|n
argument_list|)
decl_stmt|;
name|infos
operator|.
name|remove
argument_list|(
name|idx
argument_list|)
expr_stmt|;
block|}
name|infos
operator|.
name|commit
argument_list|(
name|fsDir
argument_list|)
expr_stmt|;
block|}
DECL|method|split
specifier|public
name|void
name|split
parameter_list|(
name|File
name|destDir
parameter_list|,
name|String
index|[]
name|segs
parameter_list|)
throws|throws
name|IOException
block|{
name|destDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|FSDirectory
name|destFSDir
init|=
name|FSDirectory
operator|.
name|open
argument_list|(
name|destDir
argument_list|)
decl_stmt|;
name|SegmentInfos
name|destInfos
init|=
operator|new
name|SegmentInfos
argument_list|(
name|codecs
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|n
range|:
name|segs
control|)
block|{
name|SegmentInfo
name|info
init|=
name|getInfo
argument_list|(
name|n
argument_list|)
decl_stmt|;
name|destInfos
operator|.
name|add
argument_list|(
name|info
argument_list|)
expr_stmt|;
comment|// now copy files over
name|List
argument_list|<
name|String
argument_list|>
name|files
init|=
name|info
operator|.
name|files
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|srcName
range|:
name|files
control|)
block|{
name|File
name|srcFile
init|=
operator|new
name|File
argument_list|(
name|dir
argument_list|,
name|srcName
argument_list|)
decl_stmt|;
name|File
name|destFile
init|=
operator|new
name|File
argument_list|(
name|destDir
argument_list|,
name|srcName
argument_list|)
decl_stmt|;
name|copyFile
argument_list|(
name|srcFile
argument_list|,
name|destFile
argument_list|)
expr_stmt|;
block|}
block|}
name|destInfos
operator|.
name|commit
argument_list|(
name|destFSDir
argument_list|)
expr_stmt|;
comment|// System.out.println("destDir:"+destDir.getAbsolutePath());
block|}
DECL|field|copyBuffer
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|copyBuffer
init|=
operator|new
name|byte
index|[
literal|32
operator|*
literal|1024
index|]
decl_stmt|;
DECL|method|copyFile
specifier|private
specifier|static
name|void
name|copyFile
parameter_list|(
name|File
name|src
parameter_list|,
name|File
name|dst
parameter_list|)
throws|throws
name|IOException
block|{
name|InputStream
name|in
init|=
operator|new
name|FileInputStream
argument_list|(
name|src
argument_list|)
decl_stmt|;
name|OutputStream
name|out
init|=
operator|new
name|FileOutputStream
argument_list|(
name|dst
argument_list|)
decl_stmt|;
name|int
name|len
decl_stmt|;
while|while
condition|(
operator|(
name|len
operator|=
name|in
operator|.
name|read
argument_list|(
name|copyBuffer
argument_list|)
operator|)
operator|>
literal|0
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|copyBuffer
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
