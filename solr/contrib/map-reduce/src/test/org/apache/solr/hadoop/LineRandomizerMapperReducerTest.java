begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.hadoop
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|hadoop
package|;
end_package
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
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|LongWritable
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|NullWritable
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|Text
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mrunit
operator|.
name|mapreduce
operator|.
name|MapReduceDriver
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mrunit
operator|.
name|types
operator|.
name|Pair
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
begin_class
DECL|class|LineRandomizerMapperReducerTest
specifier|public
class|class
name|LineRandomizerMapperReducerTest
extends|extends
name|Assert
block|{
DECL|field|mapReduceDriver
specifier|private
name|MapReduceDriver
argument_list|<
name|LongWritable
argument_list|,
name|Text
argument_list|,
name|LongWritable
argument_list|,
name|Text
argument_list|,
name|Text
argument_list|,
name|NullWritable
argument_list|>
name|mapReduceDriver
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|LineRandomizerMapper
name|mapper
init|=
operator|new
name|LineRandomizerMapper
argument_list|()
decl_stmt|;
name|LineRandomizerReducer
name|reducer
init|=
operator|new
name|LineRandomizerReducer
argument_list|()
decl_stmt|;
name|mapReduceDriver
operator|=
name|MapReduceDriver
operator|.
name|newMapReduceDriver
argument_list|(
name|mapper
argument_list|,
name|reducer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMapReduce1Item
specifier|public
name|void
name|testMapReduce1Item
parameter_list|()
throws|throws
name|IOException
block|{
name|mapReduceDriver
operator|.
name|withInput
argument_list|(
operator|new
name|LongWritable
argument_list|(
literal|0
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
literal|"hello"
argument_list|)
argument_list|)
expr_stmt|;
name|mapReduceDriver
operator|.
name|withOutput
argument_list|(
operator|new
name|Text
argument_list|(
literal|"hello"
argument_list|)
argument_list|,
name|NullWritable
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|mapReduceDriver
operator|.
name|runTest
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMapReduce2Items
specifier|public
name|void
name|testMapReduce2Items
parameter_list|()
throws|throws
name|IOException
block|{
name|mapReduceDriver
operator|.
name|withAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Pair
argument_list|<
name|LongWritable
argument_list|,
name|Text
argument_list|>
argument_list|(
operator|new
name|LongWritable
argument_list|(
literal|0
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
literal|"hello"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|Pair
argument_list|<
name|LongWritable
argument_list|,
name|Text
argument_list|>
argument_list|(
operator|new
name|LongWritable
argument_list|(
literal|1
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
literal|"world"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|mapReduceDriver
operator|.
name|withAllOutput
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Pair
argument_list|<
name|Text
argument_list|,
name|NullWritable
argument_list|>
argument_list|(
operator|new
name|Text
argument_list|(
literal|"world"
argument_list|)
argument_list|,
name|NullWritable
operator|.
name|get
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Pair
argument_list|<
name|Text
argument_list|,
name|NullWritable
argument_list|>
argument_list|(
operator|new
name|Text
argument_list|(
literal|"hello"
argument_list|)
argument_list|,
name|NullWritable
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|mapReduceDriver
operator|.
name|runTest
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMapReduce3Items
specifier|public
name|void
name|testMapReduce3Items
parameter_list|()
throws|throws
name|IOException
block|{
name|mapReduceDriver
operator|.
name|withAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Pair
argument_list|<
name|LongWritable
argument_list|,
name|Text
argument_list|>
argument_list|(
operator|new
name|LongWritable
argument_list|(
literal|0
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
literal|"hello"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|Pair
argument_list|<
name|LongWritable
argument_list|,
name|Text
argument_list|>
argument_list|(
operator|new
name|LongWritable
argument_list|(
literal|1
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
literal|"world"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|Pair
argument_list|<
name|LongWritable
argument_list|,
name|Text
argument_list|>
argument_list|(
operator|new
name|LongWritable
argument_list|(
literal|2
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
literal|"nadja"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|mapReduceDriver
operator|.
name|withAllOutput
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Pair
argument_list|<
name|Text
argument_list|,
name|NullWritable
argument_list|>
argument_list|(
operator|new
name|Text
argument_list|(
literal|"nadja"
argument_list|)
argument_list|,
name|NullWritable
operator|.
name|get
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Pair
argument_list|<
name|Text
argument_list|,
name|NullWritable
argument_list|>
argument_list|(
operator|new
name|Text
argument_list|(
literal|"world"
argument_list|)
argument_list|,
name|NullWritable
operator|.
name|get
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Pair
argument_list|<
name|Text
argument_list|,
name|NullWritable
argument_list|>
argument_list|(
operator|new
name|Text
argument_list|(
literal|"hello"
argument_list|)
argument_list|,
name|NullWritable
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|mapReduceDriver
operator|.
name|runTest
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMapReduce4Items
specifier|public
name|void
name|testMapReduce4Items
parameter_list|()
throws|throws
name|IOException
block|{
name|mapReduceDriver
operator|.
name|withAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Pair
argument_list|<
name|LongWritable
argument_list|,
name|Text
argument_list|>
argument_list|(
operator|new
name|LongWritable
argument_list|(
literal|0
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
literal|"hello"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|Pair
argument_list|<
name|LongWritable
argument_list|,
name|Text
argument_list|>
argument_list|(
operator|new
name|LongWritable
argument_list|(
literal|1
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
literal|"world"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|Pair
argument_list|<
name|LongWritable
argument_list|,
name|Text
argument_list|>
argument_list|(
operator|new
name|LongWritable
argument_list|(
literal|2
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
literal|"nadja"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|Pair
argument_list|<
name|LongWritable
argument_list|,
name|Text
argument_list|>
argument_list|(
operator|new
name|LongWritable
argument_list|(
literal|3
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
literal|"basti"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|mapReduceDriver
operator|.
name|withAllOutput
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Pair
argument_list|<
name|Text
argument_list|,
name|NullWritable
argument_list|>
argument_list|(
operator|new
name|Text
argument_list|(
literal|"nadja"
argument_list|)
argument_list|,
name|NullWritable
operator|.
name|get
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Pair
argument_list|<
name|Text
argument_list|,
name|NullWritable
argument_list|>
argument_list|(
operator|new
name|Text
argument_list|(
literal|"world"
argument_list|)
argument_list|,
name|NullWritable
operator|.
name|get
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Pair
argument_list|<
name|Text
argument_list|,
name|NullWritable
argument_list|>
argument_list|(
operator|new
name|Text
argument_list|(
literal|"basti"
argument_list|)
argument_list|,
name|NullWritable
operator|.
name|get
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Pair
argument_list|<
name|Text
argument_list|,
name|NullWritable
argument_list|>
argument_list|(
operator|new
name|Text
argument_list|(
literal|"hello"
argument_list|)
argument_list|,
name|NullWritable
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|mapReduceDriver
operator|.
name|runTest
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
