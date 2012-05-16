begin_unit
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
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
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
name|util
operator|.
name|_TestUtil
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
name|codecs
operator|.
name|FieldInfosReader
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
name|FieldInfosWriter
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
name|document
operator|.
name|Document
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
name|FieldInfo
operator|.
name|IndexOptions
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
name|Arrays
import|;
end_import
begin_comment
comment|//import org.cnlp.utils.properties.ResourceBundleHelper;
end_comment
begin_class
DECL|class|TestFieldInfos
specifier|public
class|class
name|TestFieldInfos
extends|extends
name|LuceneTestCase
block|{
DECL|field|testDoc
specifier|private
name|Document
name|testDoc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|DocHelper
operator|.
name|setupDoc
argument_list|(
name|testDoc
argument_list|)
expr_stmt|;
block|}
DECL|method|createAndWriteFieldInfos
specifier|public
name|FieldInfos
name|createAndWriteFieldInfos
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|filename
parameter_list|)
throws|throws
name|IOException
block|{
comment|//Positive test of FieldInfos
name|assertTrue
argument_list|(
name|testDoc
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|MutableFieldInfos
name|fieldInfos
init|=
operator|new
name|MutableFieldInfos
argument_list|(
operator|new
name|MutableFieldInfos
operator|.
name|FieldNumberBiMap
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|IndexableField
name|field
range|:
name|testDoc
control|)
block|{
name|fieldInfos
operator|.
name|addOrUpdate
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|,
name|field
operator|.
name|fieldType
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//Since the complement is stored as well in the fields map
name|assertTrue
argument_list|(
name|fieldInfos
operator|.
name|size
argument_list|()
operator|==
name|DocHelper
operator|.
name|all
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|//this is all b/c we are using the no-arg constructor
name|IndexOutput
name|output
init|=
name|dir
operator|.
name|createOutput
argument_list|(
name|filename
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|output
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|//Use a RAMOutputStream
name|FieldInfosWriter
name|writer
init|=
name|Codec
operator|.
name|getDefault
argument_list|()
operator|.
name|fieldInfosFormat
argument_list|()
operator|.
name|getFieldInfosWriter
argument_list|()
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|dir
argument_list|,
name|filename
argument_list|,
name|fieldInfos
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|fieldInfos
return|;
block|}
DECL|method|readFieldInfos
specifier|public
name|FieldInfos
name|readFieldInfos
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|filename
parameter_list|)
throws|throws
name|IOException
block|{
name|FieldInfosReader
name|reader
init|=
name|Codec
operator|.
name|getDefault
argument_list|()
operator|.
name|fieldInfosFormat
argument_list|()
operator|.
name|getFieldInfosReader
argument_list|()
decl_stmt|;
return|return
name|reader
operator|.
name|read
argument_list|(
name|dir
argument_list|,
name|filename
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
return|;
block|}
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|name
init|=
literal|"testFile"
decl_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|FieldInfos
name|fieldInfos
init|=
name|createAndWriteFieldInfos
argument_list|(
name|dir
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|FieldInfos
name|readIn
init|=
name|readFieldInfos
argument_list|(
name|dir
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fieldInfos
operator|.
name|size
argument_list|()
operator|==
name|readIn
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|FieldInfo
name|info
init|=
name|readIn
operator|.
name|fieldInfo
argument_list|(
literal|"textField1"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|info
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|info
operator|.
name|hasVectors
argument_list|()
operator|==
literal|false
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|info
operator|.
name|omitsNorms
argument_list|()
operator|==
literal|false
argument_list|)
expr_stmt|;
name|info
operator|=
name|readIn
operator|.
name|fieldInfo
argument_list|(
literal|"textField2"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|info
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|info
operator|.
name|omitsNorms
argument_list|()
operator|==
literal|false
argument_list|)
expr_stmt|;
name|info
operator|=
name|readIn
operator|.
name|fieldInfo
argument_list|(
literal|"textField3"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|info
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|info
operator|.
name|hasVectors
argument_list|()
operator|==
literal|false
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|info
operator|.
name|omitsNorms
argument_list|()
operator|==
literal|true
argument_list|)
expr_stmt|;
name|info
operator|=
name|readIn
operator|.
name|fieldInfo
argument_list|(
literal|"omitNorms"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|info
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|info
operator|.
name|hasVectors
argument_list|()
operator|==
literal|false
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|info
operator|.
name|omitsNorms
argument_list|()
operator|==
literal|true
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testReadOnly
specifier|public
name|void
name|testReadOnly
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|name
init|=
literal|"testFile"
decl_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|FieldInfos
name|fieldInfos
init|=
name|createAndWriteFieldInfos
argument_list|(
name|dir
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|FieldInfos
name|readOnly
init|=
name|readFieldInfos
argument_list|(
name|dir
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|assertReadOnly
argument_list|(
name|readOnly
argument_list|,
name|fieldInfos
argument_list|)
expr_stmt|;
name|FieldInfos
name|readOnlyClone
init|=
name|readOnly
operator|.
name|clone
argument_list|()
decl_stmt|;
name|assertNotSame
argument_list|(
name|readOnly
argument_list|,
name|readOnlyClone
argument_list|)
expr_stmt|;
comment|// clone is also read only - no global field map
name|assertReadOnly
argument_list|(
name|readOnlyClone
argument_list|,
name|fieldInfos
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|assertReadOnly
specifier|private
name|void
name|assertReadOnly
parameter_list|(
name|FieldInfos
name|readOnly
parameter_list|,
name|FieldInfos
name|modifiable
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|modifiable
operator|.
name|size
argument_list|()
argument_list|,
name|readOnly
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// assert we can iterate
for|for
control|(
name|FieldInfo
name|fi
range|:
name|readOnly
control|)
block|{
name|assertEquals
argument_list|(
name|fi
operator|.
name|name
argument_list|,
name|modifiable
operator|.
name|fieldInfo
argument_list|(
name|fi
operator|.
name|number
argument_list|)
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
