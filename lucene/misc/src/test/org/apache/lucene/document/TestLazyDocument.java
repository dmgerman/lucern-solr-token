begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|store
operator|.
name|*
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
name|*
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
name|*
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
name|search
operator|.
name|*
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import
begin_class
DECL|class|TestLazyDocument
specifier|public
class|class
name|TestLazyDocument
extends|extends
name|LuceneTestCase
block|{
DECL|field|NUM_DOCS
specifier|public
specifier|final
name|int
name|NUM_DOCS
init|=
name|atLeast
argument_list|(
literal|10
argument_list|)
decl_stmt|;
DECL|field|FIELDS
specifier|public
specifier|final
name|String
index|[]
name|FIELDS
init|=
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"b"
block|,
literal|"c"
block|,
literal|"d"
block|,
literal|"e"
block|,
literal|"f"
block|,
literal|"g"
block|,
literal|"h"
block|,
literal|"i"
block|,
literal|"j"
block|,
literal|"k"
block|}
decl_stmt|;
DECL|field|NUM_VALUES
specifier|public
specifier|final
name|int
name|NUM_VALUES
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
DECL|field|dir
specifier|public
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
annotation|@
name|After
DECL|method|removeIndex
specifier|public
name|void
name|removeIndex
parameter_list|()
block|{
if|if
condition|(
literal|null
operator|!=
name|dir
condition|)
block|{
try|try
block|{
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|=
literal|null
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|/* NOOP */
block|}
block|}
block|}
annotation|@
name|Before
DECL|method|createIndex
specifier|public
name|void
name|createIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|analyzer
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|docid
init|=
literal|0
init|;
name|docid
operator|<
name|NUM_DOCS
condition|;
name|docid
operator|++
control|)
block|{
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
literal|"docid"
argument_list|,
literal|""
operator|+
name|docid
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
literal|"never_load"
argument_list|,
literal|"fail"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|f
range|:
name|FIELDS
control|)
block|{
for|for
control|(
name|int
name|val
init|=
literal|0
init|;
name|val
operator|<
name|NUM_VALUES
condition|;
name|val
operator|++
control|)
block|{
name|d
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
name|f
argument_list|,
name|docid
operator|+
literal|"_"
operator|+
name|f
operator|+
literal|"_"
operator|+
name|val
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|d
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
literal|"load_later"
argument_list|,
literal|"yes"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|writer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testLazy
specifier|public
name|void
name|testLazy
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|id
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|NUM_DOCS
argument_list|)
decl_stmt|;
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
try|try
block|{
name|Query
name|q
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"docid"
argument_list|,
literal|""
operator|+
name|id
argument_list|)
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|100
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Too many docs"
argument_list|,
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|LazyTestingStoredFieldVisitor
name|visitor
init|=
operator|new
name|LazyTestingStoredFieldVisitor
argument_list|(
operator|new
name|LazyDocument
argument_list|(
name|reader
argument_list|,
name|hits
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
argument_list|,
name|FIELDS
argument_list|)
decl_stmt|;
name|reader
operator|.
name|document
argument_list|(
name|hits
index|[
literal|0
index|]
operator|.
name|doc
argument_list|,
name|visitor
argument_list|)
expr_stmt|;
name|StoredDocument
name|d
init|=
name|visitor
operator|.
name|doc
decl_stmt|;
name|int
name|numFieldValues
init|=
literal|0
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|fieldValueCounts
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// at this point, all FIELDS should be Lazy and unrealized
for|for
control|(
name|StorableField
name|f
range|:
name|d
control|)
block|{
name|numFieldValues
operator|++
expr_stmt|;
if|if
condition|(
name|f
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
literal|"never_load"
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"never_load was loaded"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|f
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
literal|"load_later"
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"load_later was loaded on first pass"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|f
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
literal|"docid"
argument_list|)
condition|)
block|{
name|assertFalse
argument_list|(
name|f
operator|.
name|name
argument_list|()
argument_list|,
name|f
operator|instanceof
name|LazyDocument
operator|.
name|LazyField
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|count
init|=
name|fieldValueCounts
operator|.
name|containsKey
argument_list|(
name|f
operator|.
name|name
argument_list|()
argument_list|)
condition|?
name|fieldValueCounts
operator|.
name|get
argument_list|(
name|f
operator|.
name|name
argument_list|()
argument_list|)
else|:
literal|0
decl_stmt|;
name|count
operator|++
expr_stmt|;
name|fieldValueCounts
operator|.
name|put
argument_list|(
name|f
operator|.
name|name
argument_list|()
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|name
argument_list|()
operator|+
literal|" is "
operator|+
name|f
operator|.
name|getClass
argument_list|()
argument_list|,
name|f
operator|instanceof
name|LazyDocument
operator|.
name|LazyField
argument_list|)
expr_stmt|;
name|LazyDocument
operator|.
name|LazyField
name|lf
init|=
operator|(
name|LazyDocument
operator|.
name|LazyField
operator|)
name|f
decl_stmt|;
name|assertFalse
argument_list|(
name|f
operator|.
name|name
argument_list|()
operator|+
literal|" is loaded"
argument_list|,
name|lf
operator|.
name|hasBeenLoaded
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|VERBOSE
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"numFieldValues == "
operator|+
name|numFieldValues
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"numFieldValues"
argument_list|,
literal|1
operator|+
operator|(
name|NUM_VALUES
operator|*
name|FIELDS
operator|.
name|length
operator|)
argument_list|,
name|numFieldValues
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|fieldName
range|:
name|fieldValueCounts
operator|.
name|keySet
argument_list|()
control|)
block|{
name|assertEquals
argument_list|(
literal|"fieldName count: "
operator|+
name|fieldName
argument_list|,
name|NUM_VALUES
argument_list|,
operator|(
name|int
operator|)
name|fieldValueCounts
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// pick a single field name to load a single value
specifier|final
name|String
name|fieldName
init|=
name|FIELDS
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|FIELDS
operator|.
name|length
argument_list|)
index|]
decl_stmt|;
specifier|final
name|StorableField
index|[]
name|fieldValues
init|=
name|d
operator|.
name|getFields
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"#vals in field: "
operator|+
name|fieldName
argument_list|,
name|NUM_VALUES
argument_list|,
name|fieldValues
operator|.
name|length
argument_list|)
expr_stmt|;
specifier|final
name|int
name|valNum
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|fieldValues
operator|.
name|length
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|id
operator|+
literal|"_"
operator|+
name|fieldName
operator|+
literal|"_"
operator|+
name|valNum
argument_list|,
name|fieldValues
index|[
name|valNum
index|]
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// now every value of fieldName should be loaded
for|for
control|(
name|StorableField
name|f
range|:
name|d
control|)
block|{
if|if
condition|(
name|f
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
literal|"never_load"
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"never_load was loaded"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|f
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
literal|"load_later"
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"load_later was loaded too soon"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|f
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
literal|"docid"
argument_list|)
condition|)
block|{
name|assertFalse
argument_list|(
name|f
operator|.
name|name
argument_list|()
argument_list|,
name|f
operator|instanceof
name|LazyDocument
operator|.
name|LazyField
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|f
operator|.
name|name
argument_list|()
operator|+
literal|" is "
operator|+
name|f
operator|.
name|getClass
argument_list|()
argument_list|,
name|f
operator|instanceof
name|LazyDocument
operator|.
name|LazyField
argument_list|)
expr_stmt|;
name|LazyDocument
operator|.
name|LazyField
name|lf
init|=
operator|(
name|LazyDocument
operator|.
name|LazyField
operator|)
name|f
decl_stmt|;
name|assertEquals
argument_list|(
name|f
operator|.
name|name
argument_list|()
operator|+
literal|" is loaded?"
argument_list|,
name|lf
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
argument_list|,
name|lf
operator|.
name|hasBeenLoaded
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// use the same LazyDoc to ask for one more lazy field
name|visitor
operator|=
operator|new
name|LazyTestingStoredFieldVisitor
argument_list|(
operator|new
name|LazyDocument
argument_list|(
name|reader
argument_list|,
name|hits
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
argument_list|,
literal|"load_later"
argument_list|)
expr_stmt|;
name|reader
operator|.
name|document
argument_list|(
name|hits
index|[
literal|0
index|]
operator|.
name|doc
argument_list|,
name|visitor
argument_list|)
expr_stmt|;
name|d
operator|=
name|visitor
operator|.
name|doc
expr_stmt|;
comment|// ensure we have all the values we expect now, and that
comment|// adding one more lazy field didn't "unload" the existing LazyField's
comment|// we already loaded.
for|for
control|(
name|StorableField
name|f
range|:
name|d
control|)
block|{
if|if
condition|(
name|f
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
literal|"never_load"
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"never_load was loaded"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|f
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
literal|"docid"
argument_list|)
condition|)
block|{
name|assertFalse
argument_list|(
name|f
operator|.
name|name
argument_list|()
argument_list|,
name|f
operator|instanceof
name|LazyDocument
operator|.
name|LazyField
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|f
operator|.
name|name
argument_list|()
operator|+
literal|" is "
operator|+
name|f
operator|.
name|getClass
argument_list|()
argument_list|,
name|f
operator|instanceof
name|LazyDocument
operator|.
name|LazyField
argument_list|)
expr_stmt|;
name|LazyDocument
operator|.
name|LazyField
name|lf
init|=
operator|(
name|LazyDocument
operator|.
name|LazyField
operator|)
name|f
decl_stmt|;
name|assertEquals
argument_list|(
name|f
operator|.
name|name
argument_list|()
operator|+
literal|" is loaded?"
argument_list|,
name|lf
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
argument_list|,
name|lf
operator|.
name|hasBeenLoaded
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// even the underlying doc shouldn't have never_load
name|assertNull
argument_list|(
literal|"never_load was loaded in wrapped doc"
argument_list|,
name|visitor
operator|.
name|lazyDoc
operator|.
name|getDocument
argument_list|()
operator|.
name|getField
argument_list|(
literal|"never_load"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|LazyTestingStoredFieldVisitor
specifier|private
specifier|static
class|class
name|LazyTestingStoredFieldVisitor
extends|extends
name|StoredFieldVisitor
block|{
DECL|field|doc
specifier|public
specifier|final
name|StoredDocument
name|doc
init|=
operator|new
name|StoredDocument
argument_list|()
decl_stmt|;
DECL|field|lazyDoc
specifier|public
specifier|final
name|LazyDocument
name|lazyDoc
decl_stmt|;
DECL|field|lazyFieldNames
specifier|public
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|lazyFieldNames
decl_stmt|;
DECL|method|LazyTestingStoredFieldVisitor
name|LazyTestingStoredFieldVisitor
parameter_list|(
name|LazyDocument
name|l
parameter_list|,
name|String
modifier|...
name|fields
parameter_list|)
block|{
name|lazyDoc
operator|=
name|l
expr_stmt|;
name|lazyFieldNames
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|fields
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|needsField
specifier|public
name|Status
name|needsField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
if|if
condition|(
name|fieldInfo
operator|.
name|name
operator|.
name|equals
argument_list|(
literal|"docid"
argument_list|)
condition|)
block|{
return|return
name|Status
operator|.
name|YES
return|;
block|}
elseif|else
if|if
condition|(
name|fieldInfo
operator|.
name|name
operator|.
name|equals
argument_list|(
literal|"never_load"
argument_list|)
condition|)
block|{
return|return
name|Status
operator|.
name|NO
return|;
block|}
else|else
block|{
if|if
condition|(
name|lazyFieldNames
operator|.
name|contains
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|)
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|lazyDoc
operator|.
name|getField
argument_list|(
name|fieldInfo
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|Status
operator|.
name|NO
return|;
block|}
annotation|@
name|Override
DECL|method|stringField
specifier|public
name|void
name|stringField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|FieldType
name|ft
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|ft
operator|.
name|setStoreTermVectors
argument_list|(
name|fieldInfo
operator|.
name|hasVectors
argument_list|()
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setIndexed
argument_list|(
name|fieldInfo
operator|.
name|isIndexed
argument_list|()
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setOmitNorms
argument_list|(
name|fieldInfo
operator|.
name|omitsNorms
argument_list|()
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setIndexOptions
argument_list|(
name|fieldInfo
operator|.
name|getIndexOptions
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|,
name|value
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
