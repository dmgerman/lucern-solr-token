begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package
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
name|TestUtil
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
name|BytesRef
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
operator|.
name|ErrorCode
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|IndexSchema
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|SchemaField
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
operator|.
name|SolrQueryRequest
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|SolrTestCaseJ4
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|CursorPagingTest
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|CursorMarkParams
operator|.
name|CURSOR_MARK_START
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
name|Collections
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import
begin_comment
comment|/**  * Primarily a test of parsing and serialization of the CursorMark values.  *  * NOTE: this class Reuses some utilities from {@link CursorPagingTest} that assume the same schema and configs.  *  * @see CursorPagingTest   */
end_comment
begin_class
DECL|class|CursorMarkTest
specifier|public
class|class
name|CursorMarkTest
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|BeforeClass
DECL|method|beforeTests
specifier|public
specifier|static
name|void
name|beforeTests
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.test.useFilterForSortedQuery"
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|initCore
argument_list|(
name|CursorPagingTest
operator|.
name|TEST_SOLRCONFIG_NAME
argument_list|,
name|CursorPagingTest
operator|.
name|TEST_SCHEMAXML_NAME
argument_list|)
expr_stmt|;
block|}
DECL|method|testNextCursorMark
specifier|public
name|void
name|testNextCursorMark
parameter_list|()
block|{
specifier|final
name|Collection
argument_list|<
name|String
argument_list|>
name|allFieldNames
init|=
name|getAllFieldNames
argument_list|()
decl_stmt|;
specifier|final
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|()
decl_stmt|;
specifier|final
name|IndexSchema
name|schema
init|=
name|req
operator|.
name|getSchema
argument_list|()
decl_stmt|;
specifier|final
name|String
name|randomSortString
init|=
name|CursorPagingTest
operator|.
name|buildRandomSort
argument_list|(
name|allFieldNames
argument_list|)
decl_stmt|;
specifier|final
name|SortSpec
name|ss
init|=
name|QueryParsing
operator|.
name|parseSortSpec
argument_list|(
name|randomSortString
argument_list|,
name|req
argument_list|)
decl_stmt|;
specifier|final
name|CursorMark
name|previous
init|=
operator|new
name|CursorMark
argument_list|(
name|schema
argument_list|,
name|ss
argument_list|)
decl_stmt|;
name|previous
operator|.
name|parseSerializedTotem
argument_list|(
name|CURSOR_MARK_START
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Object
argument_list|>
name|nextValues
init|=
name|Arrays
operator|.
expr|<
name|Object
operator|>
name|asList
argument_list|(
name|buildRandomSortObjects
argument_list|(
name|ss
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|CursorMark
name|next
init|=
name|previous
operator|.
name|createNext
argument_list|(
name|nextValues
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"next values not correct"
argument_list|,
name|nextValues
argument_list|,
name|next
operator|.
name|getSortValues
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"next SortSpec not correct"
argument_list|,
name|ss
argument_list|,
name|next
operator|.
name|getSortSpec
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
comment|// append to our random sort string so we know it has wrong num clauses
specifier|final
name|SortSpec
name|otherSort
init|=
name|QueryParsing
operator|.
name|parseSortSpec
argument_list|(
name|randomSortString
operator|+
literal|",id asc"
argument_list|,
name|req
argument_list|)
decl_stmt|;
name|CursorMark
name|trash
init|=
name|previous
operator|.
name|createNext
argument_list|(
name|Arrays
operator|.
expr|<
name|Object
operator|>
name|asList
argument_list|(
name|buildRandomSortObjects
argument_list|(
name|otherSort
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"didn't fail on next with incorrect num of sortvalues"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AssertionError
name|e
parameter_list|)
block|{
comment|// NOOP: we're happy
block|}
block|}
DECL|method|testInvalidUsage
specifier|public
name|void
name|testInvalidUsage
parameter_list|()
block|{
specifier|final
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|()
decl_stmt|;
specifier|final
name|IndexSchema
name|schema
init|=
name|req
operator|.
name|getSchema
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|SortSpec
name|ss
init|=
name|QueryParsing
operator|.
name|parseSortSpec
argument_list|(
literal|"str desc, score desc"
argument_list|,
name|req
argument_list|)
decl_stmt|;
specifier|final
name|CursorMark
name|totem
init|=
operator|new
name|CursorMark
argument_list|(
name|schema
argument_list|,
name|ss
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"no failure from sort that doesn't include uniqueKey field"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
operator|.
name|code
argument_list|,
name|e
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|0
operator|<
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|"uniqueKey"
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
specifier|final
name|String
name|dir
range|:
name|Arrays
operator|.
name|asList
argument_list|(
literal|"asc"
argument_list|,
literal|"desc"
argument_list|)
control|)
block|{
try|try
block|{
specifier|final
name|SortSpec
name|ss
init|=
name|QueryParsing
operator|.
name|parseSortSpec
argument_list|(
literal|"score "
operator|+
name|dir
argument_list|,
name|req
argument_list|)
decl_stmt|;
specifier|final
name|CursorMark
name|totem
init|=
operator|new
name|CursorMark
argument_list|(
name|schema
argument_list|,
name|ss
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"no failure from score only sort: "
operator|+
name|dir
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
operator|.
name|code
argument_list|,
name|e
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|0
operator|<
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|"uniqueKey"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
specifier|final
name|SortSpec
name|ss
init|=
name|QueryParsing
operator|.
name|parseSortSpec
argument_list|(
literal|"_docid_ "
operator|+
name|dir
operator|+
literal|", id desc"
argument_list|,
name|req
argument_list|)
decl_stmt|;
specifier|final
name|CursorMark
name|totem
init|=
operator|new
name|CursorMark
argument_list|(
name|schema
argument_list|,
name|ss
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"no failure from sort that includes _docid_: "
operator|+
name|dir
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
operator|.
name|code
argument_list|,
name|e
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|0
operator|<
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|"_docid_"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testGarbageParsing
specifier|public
name|void
name|testGarbageParsing
parameter_list|()
block|{
specifier|final
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|()
decl_stmt|;
specifier|final
name|IndexSchema
name|schema
init|=
name|req
operator|.
name|getSchema
argument_list|()
decl_stmt|;
specifier|final
name|SortSpec
name|ss
init|=
name|QueryParsing
operator|.
name|parseSortSpec
argument_list|(
literal|"str asc, float desc, id asc"
argument_list|,
name|req
argument_list|)
decl_stmt|;
specifier|final
name|CursorMark
name|totem
init|=
operator|new
name|CursorMark
argument_list|(
name|schema
argument_list|,
name|ss
argument_list|)
decl_stmt|;
comment|// totem string that isn't even valid base64
try|try
block|{
name|totem
operator|.
name|parseSerializedTotem
argument_list|(
literal|"all the documents please"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't fail on invalid base64 totem"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
operator|.
name|code
argument_list|,
name|e
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Unable to parse 'cursorMark'"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// empty totem string
try|try
block|{
name|totem
operator|.
name|parseSerializedTotem
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't fail on empty totem"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
operator|.
name|code
argument_list|,
name|e
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Unable to parse 'cursorMark'"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// whitespace-only totem string
try|try
block|{
name|totem
operator|.
name|parseSerializedTotem
argument_list|(
literal|"       "
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't fail on whitespace-only totem"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
operator|.
name|code
argument_list|,
name|e
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Unable to parse 'cursorMark'"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// totem string from sort with diff num clauses
try|try
block|{
specifier|final
name|SortSpec
name|otherSort
init|=
name|QueryParsing
operator|.
name|parseSortSpec
argument_list|(
literal|"double desc, id asc"
argument_list|,
name|req
argument_list|)
decl_stmt|;
specifier|final
name|CursorMark
name|otherTotem
init|=
operator|new
name|CursorMark
argument_list|(
name|schema
argument_list|,
name|otherSort
argument_list|)
decl_stmt|;
name|otherTotem
operator|.
name|setSortValues
argument_list|(
name|Arrays
operator|.
expr|<
name|Object
operator|>
name|asList
argument_list|(
name|buildRandomSortObjects
argument_list|(
name|otherSort
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|totem
operator|.
name|parseSerializedTotem
argument_list|(
name|otherTotem
operator|.
name|getSerializedTotem
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't fail on totem from incorrect sort (num clauses)"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
operator|.
name|code
argument_list|,
name|e
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"wrong size"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testRoundTripParsing
specifier|public
name|void
name|testRoundTripParsing
parameter_list|()
block|{
comment|// for any valid SortSpec, and any legal values, we should be able to round
comment|// trip serialize the totem and get the same values back.
specifier|final
name|Collection
argument_list|<
name|String
argument_list|>
name|allFieldNames
init|=
name|getAllFieldNames
argument_list|()
decl_stmt|;
specifier|final
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|()
decl_stmt|;
specifier|final
name|IndexSchema
name|schema
init|=
name|req
operator|.
name|getSchema
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numRandomSorts
init|=
name|atLeast
argument_list|(
literal|50
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numRandomValIters
init|=
name|atLeast
argument_list|(
literal|10
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
name|numRandomSorts
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|SortSpec
name|ss
init|=
name|QueryParsing
operator|.
name|parseSortSpec
argument_list|(
name|CursorPagingTest
operator|.
name|buildRandomSort
argument_list|(
name|allFieldNames
argument_list|)
argument_list|,
name|req
argument_list|)
decl_stmt|;
specifier|final
name|CursorMark
name|totemIn
init|=
operator|new
name|CursorMark
argument_list|(
name|schema
argument_list|,
name|ss
argument_list|)
decl_stmt|;
specifier|final
name|CursorMark
name|totemOut
init|=
operator|new
name|CursorMark
argument_list|(
name|schema
argument_list|,
name|ss
argument_list|)
decl_stmt|;
comment|// trivial case: regardless of sort, "*" should be valid and roundtrippable
name|totemIn
operator|.
name|parseSerializedTotem
argument_list|(
name|CURSOR_MARK_START
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|CURSOR_MARK_START
argument_list|,
name|totemIn
operator|.
name|getSerializedTotem
argument_list|()
argument_list|)
expr_stmt|;
comment|// values should be null (and still roundtrippable)
name|assertNull
argument_list|(
name|totemIn
operator|.
name|getSortValues
argument_list|()
argument_list|)
expr_stmt|;
name|totemOut
operator|.
name|setSortValues
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|CURSOR_MARK_START
argument_list|,
name|totemOut
operator|.
name|getSerializedTotem
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numRandomValIters
condition|;
name|j
operator|++
control|)
block|{
specifier|final
name|Object
index|[]
name|inValues
init|=
name|buildRandomSortObjects
argument_list|(
name|ss
argument_list|)
decl_stmt|;
name|totemIn
operator|.
name|setSortValues
argument_list|(
name|Arrays
operator|.
expr|<
name|Object
operator|>
name|asList
argument_list|(
name|inValues
argument_list|)
argument_list|)
expr_stmt|;
name|totemOut
operator|.
name|parseSerializedTotem
argument_list|(
name|totemIn
operator|.
name|getSerializedTotem
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|out
init|=
name|totemOut
operator|.
name|getSortValues
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|out
argument_list|)
expr_stmt|;
specifier|final
name|Object
index|[]
name|outValues
init|=
name|out
operator|.
name|toArray
argument_list|()
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|inValues
argument_list|,
name|outValues
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|buildRandomSortObjects
specifier|private
specifier|static
name|Object
index|[]
name|buildRandomSortObjects
parameter_list|(
name|SortSpec
name|ss
parameter_list|)
block|{
name|List
argument_list|<
name|SchemaField
argument_list|>
name|fields
init|=
name|ss
operator|.
name|getSchemaFields
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|fields
argument_list|)
expr_stmt|;
name|Object
index|[]
name|results
init|=
operator|new
name|Object
index|[
name|fields
operator|.
name|size
argument_list|()
index|]
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
name|results
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|SchemaField
name|sf
init|=
name|fields
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|sf
condition|)
block|{
comment|// score or function
name|results
index|[
name|i
index|]
operator|=
operator|(
name|Float
operator|)
name|random
argument_list|()
operator|.
name|nextFloat
argument_list|()
operator|*
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
expr_stmt|;
break|break;
block|}
elseif|else
if|if
condition|(
literal|0
operator|==
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|7
argument_list|)
condition|)
block|{
comment|// emulate missing value for doc
name|results
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|String
name|fieldName
init|=
name|sf
operator|.
name|getName
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
comment|// Note: In some cases we build a human readable version of the sort value and then
comment|// unmarshall it into the raw, real, sort values that are expected by the FieldTypes.
comment|// In other cases we just build the raw value to begin with because it's easier
name|Object
name|val
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|fieldName
operator|.
name|equals
argument_list|(
literal|"id"
argument_list|)
condition|)
block|{
name|val
operator|=
name|sf
operator|.
name|getType
argument_list|()
operator|.
name|unmarshalSortValue
argument_list|(
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fieldName
operator|.
name|startsWith
argument_list|(
literal|"str"
argument_list|)
condition|)
block|{
name|val
operator|=
name|sf
operator|.
name|getType
argument_list|()
operator|.
name|unmarshalSortValue
argument_list|(
name|TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fieldName
operator|.
name|startsWith
argument_list|(
literal|"bin"
argument_list|)
condition|)
block|{
name|byte
index|[]
name|randBytes
init|=
operator|new
name|byte
index|[
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|50
argument_list|)
index|]
decl_stmt|;
name|random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|randBytes
argument_list|)
expr_stmt|;
name|val
operator|=
operator|new
name|BytesRef
argument_list|(
name|randBytes
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fieldName
operator|.
name|startsWith
argument_list|(
literal|"int"
argument_list|)
condition|)
block|{
name|val
operator|=
operator|(
name|Integer
operator|)
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fieldName
operator|.
name|startsWith
argument_list|(
literal|"long"
argument_list|)
condition|)
block|{
name|val
operator|=
operator|(
name|Long
operator|)
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fieldName
operator|.
name|startsWith
argument_list|(
literal|"float"
argument_list|)
condition|)
block|{
name|val
operator|=
operator|(
name|Float
operator|)
name|random
argument_list|()
operator|.
name|nextFloat
argument_list|()
operator|*
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
expr_stmt|;
break|break;
block|}
elseif|else
if|if
condition|(
name|fieldName
operator|.
name|startsWith
argument_list|(
literal|"double"
argument_list|)
condition|)
block|{
name|val
operator|=
operator|(
name|Double
operator|)
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
operator|*
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
expr_stmt|;
break|break;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"fell through the rabbit hole, new field in schema? = "
operator|+
name|fieldName
argument_list|)
expr_stmt|;
block|}
name|results
index|[
name|i
index|]
operator|=
name|val
expr_stmt|;
block|}
block|}
return|return
name|results
return|;
block|}
comment|/**    * a list of the fields in the schema - excluding _version_    */
DECL|method|getAllFieldNames
specifier|private
name|Collection
argument_list|<
name|String
argument_list|>
name|getAllFieldNames
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
literal|37
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|f
range|:
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getLatestSchema
argument_list|()
operator|.
name|getFields
argument_list|()
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|f
operator|.
name|equals
argument_list|(
literal|"_version_"
argument_list|)
condition|)
block|{
name|names
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|Collections
operator|.
expr|<
name|String
operator|>
name|unmodifiableCollection
argument_list|(
name|names
argument_list|)
return|;
block|}
block|}
end_class
end_unit
