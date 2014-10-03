begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.lucene40
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene40
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
name|org
operator|.
name|apache
operator|.
name|lucene
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
name|BaseCompoundFormatTestCase
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
name|MockDirectoryWrapper
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
name|IndexInput
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
name|IndexOutput
import|;
end_import
begin_class
DECL|class|TestLucene40CompoundFormat
specifier|public
class|class
name|TestLucene40CompoundFormat
extends|extends
name|BaseCompoundFormatTestCase
block|{
DECL|field|codec
specifier|private
specifier|final
name|Codec
name|codec
init|=
operator|new
name|Lucene40RWCodec
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|getCodec
specifier|protected
name|Codec
name|getCodec
parameter_list|()
block|{
return|return
name|codec
return|;
block|}
comment|// LUCENE-3382 test that delegate compound files correctly.
DECL|method|testCompoundFileAppendTwice
specifier|public
name|void
name|testCompoundFileAppendTwice
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|newDir
init|=
name|newFSDirectory
argument_list|(
name|createTempDir
argument_list|(
literal|"testCompoundFileAppendTwice"
argument_list|)
argument_list|)
decl_stmt|;
name|Directory
name|csw
init|=
operator|new
name|Lucene40CompoundReader
argument_list|(
name|newDir
argument_list|,
literal|"d.cfs"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|createSequenceFile
argument_list|(
name|newDir
argument_list|,
literal|"d1"
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|,
literal|15
argument_list|)
expr_stmt|;
name|IndexOutput
name|out
init|=
name|csw
operator|.
name|createOutput
argument_list|(
literal|"d.xyz"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|csw
operator|.
name|listAll
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"d.xyz"
argument_list|,
name|csw
operator|.
name|listAll
argument_list|()
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|csw
operator|.
name|close
argument_list|()
expr_stmt|;
name|Directory
name|cfr
init|=
operator|new
name|Lucene40CompoundReader
argument_list|(
name|newDir
argument_list|,
literal|"d.cfs"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|cfr
operator|.
name|listAll
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"d.xyz"
argument_list|,
name|cfr
operator|.
name|listAll
argument_list|()
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|cfr
operator|.
name|close
argument_list|()
expr_stmt|;
name|newDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testReadNestedCFP
specifier|public
name|void
name|testReadNestedCFP
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|newDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
comment|// manually manipulates directory
if|if
condition|(
name|newDir
operator|instanceof
name|MockDirectoryWrapper
condition|)
block|{
operator|(
operator|(
name|MockDirectoryWrapper
operator|)
name|newDir
operator|)
operator|.
name|setEnableVirusScanner
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|Lucene40CompoundReader
name|csw
init|=
operator|new
name|Lucene40CompoundReader
argument_list|(
name|newDir
argument_list|,
literal|"d.cfs"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Lucene40CompoundReader
name|nested
init|=
operator|new
name|Lucene40CompoundReader
argument_list|(
name|newDir
argument_list|,
literal|"b.cfs"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|IndexOutput
name|out
init|=
name|nested
operator|.
name|createOutput
argument_list|(
literal|"b.xyz"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|IndexOutput
name|out1
init|=
name|nested
operator|.
name|createOutput
argument_list|(
literal|"b_1.xyz"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|out1
operator|.
name|writeInt
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|out1
operator|.
name|close
argument_list|()
expr_stmt|;
name|nested
operator|.
name|close
argument_list|()
expr_stmt|;
name|newDir
operator|.
name|copy
argument_list|(
name|csw
argument_list|,
literal|"b.cfs"
argument_list|,
literal|"b.cfs"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|newDir
operator|.
name|copy
argument_list|(
name|csw
argument_list|,
literal|"b.cfe"
argument_list|,
literal|"b.cfe"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|newDir
operator|.
name|deleteFile
argument_list|(
literal|"b.cfs"
argument_list|)
expr_stmt|;
name|newDir
operator|.
name|deleteFile
argument_list|(
literal|"b.cfe"
argument_list|)
expr_stmt|;
name|csw
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|newDir
operator|.
name|listAll
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|csw
operator|=
operator|new
name|Lucene40CompoundReader
argument_list|(
name|newDir
argument_list|,
literal|"d.cfs"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|csw
operator|.
name|listAll
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|nested
operator|=
operator|new
name|Lucene40CompoundReader
argument_list|(
name|csw
argument_list|,
literal|"b.cfs"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|nested
operator|.
name|listAll
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|IndexInput
name|openInput
init|=
name|nested
operator|.
name|openInput
argument_list|(
literal|"b.xyz"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|openInput
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
name|openInput
operator|.
name|close
argument_list|()
expr_stmt|;
name|openInput
operator|=
name|nested
operator|.
name|openInput
argument_list|(
literal|"b_1.xyz"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|openInput
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
name|openInput
operator|.
name|close
argument_list|()
expr_stmt|;
name|nested
operator|.
name|close
argument_list|()
expr_stmt|;
name|csw
operator|.
name|close
argument_list|()
expr_stmt|;
name|newDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Creates a file of the specified size with sequential data. The first    *  byte is written as the start byte provided. All subsequent bytes are    *  computed as start + offset where offset is the number of the byte.    */
DECL|method|createSequenceFile
specifier|private
name|void
name|createSequenceFile
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|name
parameter_list|,
name|byte
name|start
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexOutput
name|os
init|=
name|dir
operator|.
name|createOutput
argument_list|(
name|name
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|os
operator|.
name|writeByte
argument_list|(
name|start
argument_list|)
expr_stmt|;
name|start
operator|++
expr_stmt|;
block|}
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
