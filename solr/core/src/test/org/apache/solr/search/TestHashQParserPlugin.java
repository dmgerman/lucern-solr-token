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
name|FixedBitSet
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
name|common
operator|.
name|params
operator|.
name|ModifiableSolrParams
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
name|util
operator|.
name|NamedList
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
name|response
operator|.
name|SolrQueryResponse
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
name|BeforeClass
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
name|*
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import
begin_class
annotation|@
name|LuceneTestCase
operator|.
name|SuppressCodecs
argument_list|(
block|{
literal|"Lucene3x"
block|,
literal|"Lucene40"
block|,
literal|"Lucene41"
block|,
literal|"Lucene42"
block|,
literal|"Lucene45"
block|}
argument_list|)
DECL|class|TestHashQParserPlugin
specifier|public
class|class
name|TestHashQParserPlugin
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig-hash.xml"
argument_list|,
literal|"schema-hash.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
comment|// if you override setUp or tearDown, you better call
comment|// the super classes version
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|clearIndex
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getCost
specifier|public
name|int
name|getCost
parameter_list|(
name|Random
name|random
parameter_list|)
block|{
name|int
name|i
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
return|return
literal|200
return|;
block|}
else|else
block|{
return|return
literal|1
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testHashPartition
specifier|public
name|void
name|testHashPartition
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
name|HashSet
argument_list|<
name|String
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|()
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
literal|50
condition|;
name|i
operator|++
control|)
block|{
name|int
name|v
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|1000000
argument_list|)
decl_stmt|;
name|String
name|val
init|=
name|Integer
operator|.
name|toString
argument_list|(
name|v
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|set
operator|.
name|contains
argument_list|(
name|val
argument_list|)
condition|)
block|{
name|set
operator|.
name|add
argument_list|(
name|val
argument_list|)
expr_stmt|;
name|String
index|[]
name|doc
init|=
block|{
literal|"id"
block|,
name|val
block|,
literal|"a_s"
block|,
name|val
block|,
literal|"a_i"
block|,
name|val
block|,
literal|"a_l"
block|,
name|val
block|}
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|%
literal|10
operator|==
literal|0
condition|)
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|//Test with 3 worker and String hash ID.
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fq"
argument_list|,
literal|"{!hash worker=0 workers=3 cost="
operator|+
name|getCost
argument_list|(
name|random
argument_list|)
operator|+
literal|"}"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"partitionKeys"
argument_list|,
literal|"a_s"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"rows"
argument_list|,
literal|"50"
argument_list|)
expr_stmt|;
name|HashSet
name|set1
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
name|String
name|response
init|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|set
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|s
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|results
init|=
name|h
operator|.
name|validateXPath
argument_list|(
name|response
argument_list|,
literal|"*[count(//int[@name='id'][.='"
operator|+
name|s
operator|+
literal|"'])=1]"
argument_list|)
decl_stmt|;
if|if
condition|(
name|results
operator|==
literal|null
condition|)
block|{
name|set1
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fq"
argument_list|,
literal|"{!hash worker=1 workers=3 cost="
operator|+
name|getCost
argument_list|(
name|random
argument_list|)
operator|+
literal|"}"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"partitionKeys"
argument_list|,
literal|"a_s"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"rows"
argument_list|,
literal|"50"
argument_list|)
expr_stmt|;
name|HashSet
name|set2
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
name|response
operator|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|)
expr_stmt|;
name|it
operator|=
name|set
operator|.
name|iterator
argument_list|()
expr_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|s
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|results
init|=
name|h
operator|.
name|validateXPath
argument_list|(
name|response
argument_list|,
literal|"*[count(//int[@name='id'][.='"
operator|+
name|s
operator|+
literal|"'])=1]"
argument_list|)
decl_stmt|;
if|if
condition|(
name|results
operator|==
literal|null
condition|)
block|{
name|set2
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fq"
argument_list|,
literal|"{!hash worker=2 workers=3 cost="
operator|+
name|getCost
argument_list|(
name|random
argument_list|)
operator|+
literal|"}"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"partitionKeys"
argument_list|,
literal|"a_s"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"rows"
argument_list|,
literal|"50"
argument_list|)
expr_stmt|;
name|HashSet
name|set3
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
name|response
operator|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|)
expr_stmt|;
name|it
operator|=
name|set
operator|.
name|iterator
argument_list|()
expr_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|s
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|results
init|=
name|h
operator|.
name|validateXPath
argument_list|(
name|response
argument_list|,
literal|"*[count(//int[@name='id'][.='"
operator|+
name|s
operator|+
literal|"'])=1]"
argument_list|)
decl_stmt|;
if|if
condition|(
name|results
operator|==
literal|null
condition|)
block|{
name|set3
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
assert|assert
operator|(
name|set1
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|)
assert|;
assert|assert
operator|(
name|set2
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|)
assert|;
assert|assert
operator|(
name|set3
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|)
assert|;
assert|assert
operator|(
name|set1
operator|.
name|size
argument_list|()
operator|+
name|set2
operator|.
name|size
argument_list|()
operator|+
name|set3
operator|.
name|size
argument_list|()
operator|==
name|set
operator|.
name|size
argument_list|()
operator|)
assert|;
name|assertNoOverLap
argument_list|(
name|set1
argument_list|,
name|set2
argument_list|)
expr_stmt|;
name|assertNoOverLap
argument_list|(
name|set1
argument_list|,
name|set3
argument_list|)
expr_stmt|;
name|assertNoOverLap
argument_list|(
name|set2
argument_list|,
name|set3
argument_list|)
expr_stmt|;
comment|//Test with 2 workers and int partition Key
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fq"
argument_list|,
literal|"{!hash worker=0 workers=2 cost="
operator|+
name|getCost
argument_list|(
name|random
argument_list|)
operator|+
literal|"}"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"partitionKeys"
argument_list|,
literal|"a_i"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"rows"
argument_list|,
literal|"50"
argument_list|)
expr_stmt|;
name|set1
operator|=
operator|new
name|HashSet
argument_list|()
expr_stmt|;
name|response
operator|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|)
expr_stmt|;
name|it
operator|=
name|set
operator|.
name|iterator
argument_list|()
expr_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|s
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|results
init|=
name|h
operator|.
name|validateXPath
argument_list|(
name|response
argument_list|,
literal|"*[count(//int[@name='id'][.='"
operator|+
name|s
operator|+
literal|"'])=1]"
argument_list|)
decl_stmt|;
if|if
condition|(
name|results
operator|==
literal|null
condition|)
block|{
name|set1
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fq"
argument_list|,
literal|"{!hash worker=1 workers=2 cost="
operator|+
name|getCost
argument_list|(
name|random
argument_list|)
operator|+
literal|"}"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"partitionKeys"
argument_list|,
literal|"a_i"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"rows"
argument_list|,
literal|"50"
argument_list|)
expr_stmt|;
name|set2
operator|=
operator|new
name|HashSet
argument_list|()
expr_stmt|;
name|response
operator|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|)
expr_stmt|;
name|it
operator|=
name|set
operator|.
name|iterator
argument_list|()
expr_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|s
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|results
init|=
name|h
operator|.
name|validateXPath
argument_list|(
name|response
argument_list|,
literal|"*[count(//int[@name='id'][.='"
operator|+
name|s
operator|+
literal|"'])=1]"
argument_list|)
decl_stmt|;
if|if
condition|(
name|results
operator|==
literal|null
condition|)
block|{
name|set2
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
assert|assert
operator|(
name|set1
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|)
assert|;
assert|assert
operator|(
name|set2
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|)
assert|;
assert|assert
operator|(
name|set1
operator|.
name|size
argument_list|()
operator|+
name|set2
operator|.
name|size
argument_list|()
operator|==
name|set
operator|.
name|size
argument_list|()
operator|)
assert|;
name|assertNoOverLap
argument_list|(
name|set1
argument_list|,
name|set2
argument_list|)
expr_stmt|;
comment|//Test with 2 workers and compound partition Key
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fq"
argument_list|,
literal|"{!hash worker=0 workers=2 cost="
operator|+
name|getCost
argument_list|(
name|random
argument_list|)
operator|+
literal|"}"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"partitionKeys"
argument_list|,
literal|"a_s,a_i,a_l"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"rows"
argument_list|,
literal|"50"
argument_list|)
expr_stmt|;
name|set1
operator|=
operator|new
name|HashSet
argument_list|()
expr_stmt|;
name|response
operator|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|)
expr_stmt|;
name|it
operator|=
name|set
operator|.
name|iterator
argument_list|()
expr_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|s
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|results
init|=
name|h
operator|.
name|validateXPath
argument_list|(
name|response
argument_list|,
literal|"*[count(//int[@name='id'][.='"
operator|+
name|s
operator|+
literal|"'])=1]"
argument_list|)
decl_stmt|;
if|if
condition|(
name|results
operator|==
literal|null
condition|)
block|{
name|set1
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fq"
argument_list|,
literal|"{!hash worker=1 workers=2 cost="
operator|+
name|getCost
argument_list|(
name|random
argument_list|)
operator|+
literal|"}"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"partitionKeys"
argument_list|,
literal|"a_s,a_i,a_l"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"rows"
argument_list|,
literal|"50"
argument_list|)
expr_stmt|;
name|set2
operator|=
operator|new
name|HashSet
argument_list|()
expr_stmt|;
name|response
operator|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|)
expr_stmt|;
name|it
operator|=
name|set
operator|.
name|iterator
argument_list|()
expr_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|s
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|results
init|=
name|h
operator|.
name|validateXPath
argument_list|(
name|response
argument_list|,
literal|"*[count(//int[@name='id'][.='"
operator|+
name|s
operator|+
literal|"'])=1]"
argument_list|)
decl_stmt|;
if|if
condition|(
name|results
operator|==
literal|null
condition|)
block|{
name|set2
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
assert|assert
operator|(
name|set1
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|)
assert|;
assert|assert
operator|(
name|set2
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|)
assert|;
assert|assert
operator|(
name|set1
operator|.
name|size
argument_list|()
operator|+
name|set2
operator|.
name|size
argument_list|()
operator|==
name|set
operator|.
name|size
argument_list|()
operator|)
assert|;
name|assertNoOverLap
argument_list|(
name|set1
argument_list|,
name|set2
argument_list|)
expr_stmt|;
block|}
DECL|method|assertNoOverLap
specifier|private
name|void
name|assertNoOverLap
parameter_list|(
name|Set
name|setA
parameter_list|,
name|Set
name|setB
parameter_list|)
throws|throws
name|Exception
block|{
name|Iterator
name|it
init|=
name|setA
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Object
name|o
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|setB
operator|.
name|contains
argument_list|(
name|o
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Overlapping sets for value:"
operator|+
name|o
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class
end_unit
