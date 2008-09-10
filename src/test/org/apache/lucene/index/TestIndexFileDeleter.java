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
name|analysis
operator|.
name|WhitespaceAnalyzer
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
name|RAMDirectory
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
name|document
operator|.
name|Field
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|*
import|;
end_import
begin_comment
comment|/*   Verify we can read the pre-2.1 file format, do searches   against it, and add documents to it. */
end_comment
begin_class
DECL|class|TestIndexFileDeleter
specifier|public
class|class
name|TestIndexFileDeleter
extends|extends
name|LuceneTestCase
block|{
DECL|method|testDeleteLeftoverFiles
specifier|public
name|void
name|testDeleteLeftoverFiles
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
decl_stmt|;
name|writer
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|int
name|i
decl_stmt|;
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
literal|35
condition|;
name|i
operator|++
control|)
block|{
name|addDoc
argument_list|(
name|writer
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|setUseCompoundFile
argument_list|(
literal|false
argument_list|)
expr_stmt|;
for|for
control|(
init|;
name|i
operator|<
literal|45
condition|;
name|i
operator|++
control|)
block|{
name|addDoc
argument_list|(
name|writer
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Delete one doc so we get a .del file:
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|Term
name|searchTerm
init|=
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"7"
argument_list|)
decl_stmt|;
name|int
name|delCount
init|=
name|reader
operator|.
name|deleteDocuments
argument_list|(
name|searchTerm
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"didn't delete the right number of documents"
argument_list|,
literal|1
argument_list|,
name|delCount
argument_list|)
expr_stmt|;
comment|// Set one norm so we get a .s0 file:
name|reader
operator|.
name|setNorm
argument_list|(
literal|21
argument_list|,
literal|"content"
argument_list|,
operator|(
name|float
operator|)
literal|1.5
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Now, artificially create an extra .del file& extra
comment|// .s0 file:
name|String
index|[]
name|files
init|=
name|dir
operator|.
name|list
argument_list|()
decl_stmt|;
comment|/*     for(int j=0;j<files.length;j++) {       System.out.println(j + ": " + files[j]);     }     */
comment|// The numbering of fields can vary depending on which
comment|// JRE is in use.  On some JREs we see content bound to
comment|// field 0; on others, field 1.  So, here we have to
comment|// figure out which field number corresponds to
comment|// "content", and then set our expected file names below
comment|// accordingly:
name|CompoundFileReader
name|cfsReader
init|=
operator|new
name|CompoundFileReader
argument_list|(
name|dir
argument_list|,
literal|"_2.cfs"
argument_list|)
decl_stmt|;
name|FieldInfos
name|fieldInfos
init|=
operator|new
name|FieldInfos
argument_list|(
name|cfsReader
argument_list|,
literal|"_2.fnm"
argument_list|)
decl_stmt|;
name|int
name|contentFieldIndex
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|fieldInfos
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|FieldInfo
name|fi
init|=
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|fi
operator|.
name|name
operator|.
name|equals
argument_list|(
literal|"content"
argument_list|)
condition|)
block|{
name|contentFieldIndex
operator|=
name|i
expr_stmt|;
break|break;
block|}
block|}
name|cfsReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"could not locate the 'content' field number in the _2.cfs segment"
argument_list|,
name|contentFieldIndex
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
name|String
name|normSuffix
init|=
literal|"s"
operator|+
name|contentFieldIndex
decl_stmt|;
comment|// Create a bogus separate norms file for a
comment|// segment/field that actually has a separate norms file
comment|// already:
name|copyFile
argument_list|(
name|dir
argument_list|,
literal|"_2_1."
operator|+
name|normSuffix
argument_list|,
literal|"_2_2."
operator|+
name|normSuffix
argument_list|)
expr_stmt|;
comment|// Create a bogus separate norms file for a
comment|// segment/field that actually has a separate norms file
comment|// already, using the "not compound file" extension:
name|copyFile
argument_list|(
name|dir
argument_list|,
literal|"_2_1."
operator|+
name|normSuffix
argument_list|,
literal|"_2_2.f"
operator|+
name|contentFieldIndex
argument_list|)
expr_stmt|;
comment|// Create a bogus separate norms file for a
comment|// segment/field that does not have a separate norms
comment|// file already:
name|copyFile
argument_list|(
name|dir
argument_list|,
literal|"_2_1."
operator|+
name|normSuffix
argument_list|,
literal|"_1_1."
operator|+
name|normSuffix
argument_list|)
expr_stmt|;
comment|// Create a bogus separate norms file for a
comment|// segment/field that does not have a separate norms
comment|// file already using the "not compound file" extension:
name|copyFile
argument_list|(
name|dir
argument_list|,
literal|"_2_1."
operator|+
name|normSuffix
argument_list|,
literal|"_1_1.f"
operator|+
name|contentFieldIndex
argument_list|)
expr_stmt|;
comment|// Create a bogus separate del file for a
comment|// segment that already has a separate del file:
name|copyFile
argument_list|(
name|dir
argument_list|,
literal|"_0_1.del"
argument_list|,
literal|"_0_2.del"
argument_list|)
expr_stmt|;
comment|// Create a bogus separate del file for a
comment|// segment that does not yet have a separate del file:
name|copyFile
argument_list|(
name|dir
argument_list|,
literal|"_0_1.del"
argument_list|,
literal|"_1_1.del"
argument_list|)
expr_stmt|;
comment|// Create a bogus separate del file for a
comment|// non-existent segment:
name|copyFile
argument_list|(
name|dir
argument_list|,
literal|"_0_1.del"
argument_list|,
literal|"_188_1.del"
argument_list|)
expr_stmt|;
comment|// Create a bogus segment file:
name|copyFile
argument_list|(
name|dir
argument_list|,
literal|"_0.cfs"
argument_list|,
literal|"_188.cfs"
argument_list|)
expr_stmt|;
comment|// Create a bogus fnm file when the CFS already exists:
name|copyFile
argument_list|(
name|dir
argument_list|,
literal|"_0.cfs"
argument_list|,
literal|"_0.fnm"
argument_list|)
expr_stmt|;
comment|// Create a deletable file:
name|copyFile
argument_list|(
name|dir
argument_list|,
literal|"_0.cfs"
argument_list|,
literal|"deletable"
argument_list|)
expr_stmt|;
comment|// Create some old segments file:
name|copyFile
argument_list|(
name|dir
argument_list|,
literal|"segments_3"
argument_list|,
literal|"segments"
argument_list|)
expr_stmt|;
name|copyFile
argument_list|(
name|dir
argument_list|,
literal|"segments_3"
argument_list|,
literal|"segments_2"
argument_list|)
expr_stmt|;
comment|// Create a bogus cfs file shadowing a non-cfs segment:
name|copyFile
argument_list|(
name|dir
argument_list|,
literal|"_2.cfs"
argument_list|,
literal|"_3.cfs"
argument_list|)
expr_stmt|;
name|String
index|[]
name|filesPre
init|=
name|dir
operator|.
name|list
argument_list|()
decl_stmt|;
comment|// Open& close a writer: it should delete the above 4
comment|// files and nothing more:
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|false
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|String
index|[]
name|files2
init|=
name|dir
operator|.
name|list
argument_list|()
decl_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|files
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|files2
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|files
argument_list|,
name|files2
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"IndexFileDeleter failed to delete unreferenced extra files: should have deleted "
operator|+
operator|(
name|filesPre
operator|.
name|length
operator|-
name|files
operator|.
name|length
operator|)
operator|+
literal|" files but only deleted "
operator|+
operator|(
name|filesPre
operator|.
name|length
operator|-
name|files2
operator|.
name|length
operator|)
operator|+
literal|"; expected files:\n    "
operator|+
name|asString
argument_list|(
name|files
argument_list|)
operator|+
literal|"\n  actual files:\n    "
operator|+
name|asString
argument_list|(
name|files2
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|asString
specifier|private
name|String
name|asString
parameter_list|(
name|String
index|[]
name|l
parameter_list|)
block|{
name|String
name|s
init|=
literal|""
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
name|l
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|s
operator|+=
literal|"\n    "
expr_stmt|;
block|}
name|s
operator|+=
name|l
index|[
name|i
index|]
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
DECL|method|copyFile
specifier|public
name|void
name|copyFile
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|src
parameter_list|,
name|String
name|dest
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexInput
name|in
init|=
name|dir
operator|.
name|openInput
argument_list|(
name|src
argument_list|)
decl_stmt|;
name|IndexOutput
name|out
init|=
name|dir
operator|.
name|createOutput
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|long
name|remainder
init|=
name|in
operator|.
name|length
argument_list|()
decl_stmt|;
while|while
condition|(
name|remainder
operator|>
literal|0
condition|)
block|{
name|int
name|len
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|b
operator|.
name|length
argument_list|,
name|remainder
argument_list|)
decl_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|b
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|remainder
operator|-=
name|len
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
DECL|method|addDoc
specifier|private
name|void
name|addDoc
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|int
name|id
parameter_list|)
throws|throws
name|IOException
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"content"
argument_list|,
literal|"aaa"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
