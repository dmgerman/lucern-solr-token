begin_unit
begin_package
DECL|package|org.apache.solr.common.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
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
name|solr
operator|.
name|SolrTestCaseJ4
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
name|StringReader
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
name|Collections
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
name|Map
import|;
end_import
begin_class
DECL|class|TestJsonRecordReader
specifier|public
class|class
name|TestJsonRecordReader
extends|extends
name|SolrTestCaseJ4
block|{
DECL|method|testOneLevelSplit
specifier|public
name|void
name|testOneLevelSplit
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|json
init|=
literal|"{\n"
operator|+
literal|" \"a\":\"A\" ,\n"
operator|+
literal|" \"b\":[\n"
operator|+
literal|"     {\"c\":\"C\",\"d\":\"D\" ,\"e\": {\n"
operator|+
literal|"                         \"s\":\"S\",\n"
operator|+
literal|"                         \"t\":3}},\n"
operator|+
literal|"     {\"c\":\"C1\",\"d\":\"D1\"},\n"
operator|+
literal|"     {\"c\":\"C2\",\"d\":\"D2\"}\n"
operator|+
literal|" ]\n"
operator|+
literal|"}"
decl_stmt|;
comment|//    System.out.println(json);
comment|//    All parameters are mapped with field name
name|JsonRecordReader
name|streamer
init|=
name|JsonRecordReader
operator|.
name|getInst
argument_list|(
literal|"/b"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"a_s:/a"
argument_list|,
literal|"c_s:/b/c"
argument_list|,
literal|"d_s:/b/d"
argument_list|,
literal|"e_s:/b/e/s"
argument_list|,
literal|"e_i:/b/e/t"
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|records
init|=
name|streamer
operator|.
name|getAllRecords
argument_list|(
operator|new
name|StringReader
argument_list|(
name|json
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|records
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3l
argument_list|,
operator|(
operator|(
name|Map
operator|)
name|records
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"e_i"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"D2"
argument_list|,
operator|(
operator|(
name|Map
operator|)
name|records
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"d_s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
operator|(
operator|(
name|Map
operator|)
name|records
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"e_s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
operator|(
operator|(
name|Map
operator|)
name|records
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"e_s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
operator|(
operator|(
name|Map
operator|)
name|records
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"e_i"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
operator|(
operator|(
name|Map
operator|)
name|records
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"e_i"
argument_list|)
argument_list|)
expr_stmt|;
comment|//    All parameters but /b/c is omitted
name|streamer
operator|=
name|JsonRecordReader
operator|.
name|getInst
argument_list|(
literal|"/b"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"a:/a"
argument_list|,
literal|"d:/b/d"
argument_list|,
literal|"s:/b/e/s"
argument_list|,
literal|"t:/b/e/t"
argument_list|)
argument_list|)
expr_stmt|;
name|records
operator|=
name|streamer
operator|.
name|getAllRecords
argument_list|(
operator|new
name|StringReader
argument_list|(
name|json
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|record
range|:
name|records
control|)
block|{
name|assertNull
argument_list|(
name|record
operator|.
name|get
argument_list|(
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//one nested /b/e/* object is completely ignored
name|streamer
operator|=
name|JsonRecordReader
operator|.
name|getInst
argument_list|(
literal|"/b"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"a:/a"
argument_list|,
literal|"c:/b/c"
argument_list|,
literal|"d:/b/d"
argument_list|)
argument_list|)
expr_stmt|;
name|records
operator|=
name|streamer
operator|.
name|getAllRecords
argument_list|(
operator|new
name|StringReader
argument_list|(
name|json
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|record
range|:
name|records
control|)
block|{
name|assertNull
argument_list|(
name|record
operator|.
name|get
argument_list|(
literal|"s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|record
operator|.
name|get
argument_list|(
literal|"t"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//nested /b/e/* object is completely ignored even though /b/e is mapped
name|streamer
operator|=
name|JsonRecordReader
operator|.
name|getInst
argument_list|(
literal|"/b"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"a_s:/a"
argument_list|,
literal|"c_s:/b/c"
argument_list|,
literal|"d_s:/b/d"
argument_list|,
literal|"e:/b/e"
argument_list|)
argument_list|)
expr_stmt|;
name|records
operator|=
name|streamer
operator|.
name|getAllRecords
argument_list|(
operator|new
name|StringReader
argument_list|(
name|json
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|record
range|:
name|records
control|)
block|{
name|assertNull
argument_list|(
name|record
operator|.
name|get
argument_list|(
literal|"s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|record
operator|.
name|get
argument_list|(
literal|"t"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|record
operator|.
name|get
argument_list|(
literal|"e"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|streamer
operator|=
name|JsonRecordReader
operator|.
name|getInst
argument_list|(
literal|"/b"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"a_s:/a"
argument_list|,
literal|"c_s:/b/c"
argument_list|,
literal|"d_s:/b/d"
argument_list|,
literal|"/b/e/*"
argument_list|)
argument_list|)
expr_stmt|;
name|records
operator|=
name|streamer
operator|.
name|getAllRecords
argument_list|(
operator|new
name|StringReader
argument_list|(
name|json
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|records
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3l
argument_list|,
operator|(
operator|(
name|Map
operator|)
name|records
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"t"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"S"
argument_list|,
operator|(
operator|(
name|Map
operator|)
name|records
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
operator|(
operator|(
name|Map
operator|)
name|records
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
operator|(
operator|(
name|Map
operator|)
name|records
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"s"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testRecursiveWildCard
specifier|public
name|void
name|testRecursiveWildCard
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|json
init|=
literal|"{\n"
operator|+
literal|" \"a\":\"A\" ,\n"
operator|+
literal|" \"b\":[\n"
operator|+
literal|"     {\"c\":\"C\",\"d\":\"D\" ,\"e\": {\n"
operator|+
literal|"                         \"s\":\"S\",\n"
operator|+
literal|"                         \"t\":3 ,\"u\":{\"v\":3.1234,\"w\":false}}},\n"
operator|+
literal|"     {\"c\":\"C1\",\"d\":\"D1\"},\n"
operator|+
literal|"     {\"c\":\"C2\",\"d\":\"D2\"}\n"
operator|+
literal|" ]\n"
operator|+
literal|"}"
decl_stmt|;
name|JsonRecordReader
name|streamer
decl_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|records
decl_stmt|;
name|streamer
operator|=
name|JsonRecordReader
operator|.
name|getInst
argument_list|(
literal|"/b"
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"/b/**"
argument_list|)
argument_list|)
expr_stmt|;
name|records
operator|=
name|streamer
operator|.
name|getAllRecords
argument_list|(
operator|new
name|StringReader
argument_list|(
name|json
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|records
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"records "
operator|+
name|records
argument_list|,
literal|3l
argument_list|,
operator|(
operator|(
name|Map
operator|)
name|records
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"t"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"records "
operator|+
name|records
argument_list|,
literal|"S"
argument_list|,
operator|(
operator|(
name|Map
operator|)
name|records
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"records "
operator|+
name|records
argument_list|,
literal|3.1234
argument_list|,
operator|(
operator|(
name|Map
operator|)
name|records
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"v"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"records "
operator|+
name|records
argument_list|,
literal|false
argument_list|,
operator|(
operator|(
name|Map
operator|)
name|records
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"w"
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|record
range|:
name|records
control|)
block|{
name|assertNotNull
argument_list|(
literal|"records "
operator|+
name|records
argument_list|,
name|record
operator|.
name|get
argument_list|(
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"records "
operator|+
name|records
argument_list|,
name|record
operator|.
name|get
argument_list|(
literal|"d"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|streamer
operator|=
name|JsonRecordReader
operator|.
name|getInst
argument_list|(
literal|"/"
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"/**"
argument_list|)
argument_list|)
expr_stmt|;
name|records
operator|=
name|streamer
operator|.
name|getAllRecords
argument_list|(
operator|new
name|StringReader
argument_list|(
name|json
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|records
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
operator|(
call|(
name|List
call|)
argument_list|(
operator|(
name|Map
operator|)
name|records
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
literal|"c"
argument_list|)
operator|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
operator|(
call|(
name|List
call|)
argument_list|(
operator|(
name|Map
operator|)
name|records
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
literal|"d"
argument_list|)
operator|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"records "
operator|+
name|records
argument_list|,
literal|3l
argument_list|,
operator|(
operator|(
name|Map
operator|)
name|records
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"t"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"records "
operator|+
name|records
argument_list|,
literal|"S"
argument_list|,
operator|(
operator|(
name|Map
operator|)
name|records
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"records "
operator|+
name|records
argument_list|,
literal|"A"
argument_list|,
operator|(
operator|(
name|Map
operator|)
name|records
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"records "
operator|+
name|records
argument_list|,
literal|false
argument_list|,
operator|(
operator|(
name|Map
operator|)
name|records
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"w"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testRecursiveWildcard2
specifier|public
name|void
name|testRecursiveWildcard2
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|json
init|=
literal|"{\n"
operator|+
literal|"  \"first\": \"John\",\n"
operator|+
literal|"  \"last\": \"Doe\",\n"
operator|+
literal|"  \"grade\": 8,\n"
operator|+
literal|"  \"exams\": [\n"
operator|+
literal|"      {\n"
operator|+
literal|"        \"subject\": \"Maths\",\n"
operator|+
literal|"        \"test\"   : \"term1\",\n"
operator|+
literal|"        \"marks\":90},\n"
operator|+
literal|"        {\n"
operator|+
literal|"         \"subject\": \"Biology\",\n"
operator|+
literal|"         \"test\"   : \"term1\",\n"
operator|+
literal|"         \"marks\":86}\n"
operator|+
literal|"      ]\n"
operator|+
literal|"}"
decl_stmt|;
name|JsonRecordReader
name|streamer
decl_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|records
decl_stmt|;
name|streamer
operator|=
name|JsonRecordReader
operator|.
name|getInst
argument_list|(
literal|"/exams"
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"/**"
argument_list|)
argument_list|)
expr_stmt|;
name|records
operator|=
name|streamer
operator|.
name|getAllRecords
argument_list|(
operator|new
name|StringReader
argument_list|(
name|json
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|records
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|record
range|:
name|records
control|)
block|{
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|record
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|record
operator|.
name|containsKey
argument_list|(
literal|"subject"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|record
operator|.
name|containsKey
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|record
operator|.
name|containsKey
argument_list|(
literal|"marks"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|streamer
operator|=
name|JsonRecordReader
operator|.
name|getInst
argument_list|(
literal|"/exams"
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"$FQN:/**"
argument_list|)
argument_list|)
expr_stmt|;
name|records
operator|=
name|streamer
operator|.
name|getAllRecords
argument_list|(
operator|new
name|StringReader
argument_list|(
name|json
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|records
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|record
range|:
name|records
control|)
block|{
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|record
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|record
operator|.
name|containsKey
argument_list|(
literal|"exams.subject"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|record
operator|.
name|containsKey
argument_list|(
literal|"exams.test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|record
operator|.
name|containsKey
argument_list|(
literal|"exams.marks"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|streamer
operator|=
name|JsonRecordReader
operator|.
name|getInst
argument_list|(
literal|"/"
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"txt:/**"
argument_list|)
argument_list|)
expr_stmt|;
name|records
operator|=
name|streamer
operator|.
name|getAllRecords
argument_list|(
operator|new
name|StringReader
argument_list|(
name|json
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|records
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|9
argument_list|,
operator|(
operator|(
name|List
operator|)
name|records
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"txt"
argument_list|)
operator|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
