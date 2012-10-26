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
name|search
operator|.
name|TermQuery
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
name|BooleanQuery
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
name|BooleanClause
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
name|Query
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
name|Term
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
name|util
operator|.
name|AbstractSolrTestCase
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_comment
comment|/**  *  */
end_comment
begin_class
DECL|class|TestQueryUtils
specifier|public
class|class
name|TestQueryUtils
extends|extends
name|AbstractSolrTestCase
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
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
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
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|positive
specifier|public
name|void
name|positive
parameter_list|(
name|Query
name|q
parameter_list|)
block|{
name|assertFalse
argument_list|(
name|QueryUtils
operator|.
name|isNegative
argument_list|(
name|q
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|QueryUtils
operator|.
name|getAbs
argument_list|(
name|q
argument_list|)
operator|==
name|q
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|BooleanClause
argument_list|>
name|clauses
init|=
operator|(
name|q
operator|instanceof
name|BooleanQuery
operator|)
condition|?
operator|(
operator|(
name|BooleanQuery
operator|)
name|q
operator|)
operator|.
name|clauses
argument_list|()
else|:
literal|null
decl_stmt|;
if|if
condition|(
name|clauses
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|clauses
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|assertTrue
argument_list|(
name|QueryUtils
operator|.
name|makeQueryable
argument_list|(
name|q
argument_list|)
operator|==
name|q
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|QueryUtils
operator|.
name|makeQueryable
argument_list|(
name|q
argument_list|)
operator|==
name|q
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|negative
specifier|public
name|void
name|negative
parameter_list|(
name|Query
name|q
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|QueryUtils
operator|.
name|isNegative
argument_list|(
name|q
argument_list|)
argument_list|)
expr_stmt|;
name|Query
name|abs
init|=
name|QueryUtils
operator|.
name|getAbs
argument_list|(
name|q
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|q
operator|!=
name|abs
argument_list|)
expr_stmt|;
name|Query
name|neg2
init|=
name|QueryUtils
operator|.
name|fixNegativeQuery
argument_list|(
name|q
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|abs
operator|.
name|equals
argument_list|(
name|q
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|neg2
operator|.
name|equals
argument_list|(
name|q
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNegativeQueries
specifier|public
name|void
name|testNegativeQueries
parameter_list|()
block|{
name|TermQuery
name|tq
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"hi"
argument_list|,
literal|"there"
argument_list|)
argument_list|)
decl_stmt|;
name|TermQuery
name|tq2
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"wow"
argument_list|,
literal|"dude"
argument_list|)
argument_list|)
decl_stmt|;
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|positive
argument_list|(
name|tq
argument_list|)
expr_stmt|;
comment|// positive(bq);
name|bq
operator|.
name|add
argument_list|(
name|tq
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|positive
argument_list|(
name|bq
argument_list|)
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|tq2
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
name|positive
argument_list|(
name|bq
argument_list|)
expr_stmt|;
name|bq
operator|=
operator|new
name|BooleanQuery
argument_list|()
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|tq
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
name|negative
argument_list|(
name|bq
argument_list|)
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|tq2
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
name|negative
argument_list|(
name|bq
argument_list|)
expr_stmt|;
name|String
name|f
init|=
literal|"name"
decl_stmt|;
comment|// name is whitespace tokenized
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
name|f
argument_list|,
literal|"A"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
name|f
argument_list|,
literal|"B"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
name|f
argument_list|,
literal|"C"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
name|f
argument_list|,
literal|"C"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|,
name|f
argument_list|,
literal|"D"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"6"
argument_list|,
name|f
argument_list|,
literal|"E"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"7"
argument_list|,
name|f
argument_list|,
literal|"E"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"8"
argument_list|,
name|f
argument_list|,
literal|"E W"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"9"
argument_list|,
name|f
argument_list|,
literal|"F W"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"10"
argument_list|,
name|f
argument_list|,
literal|"G W"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"11"
argument_list|,
name|f
argument_list|,
literal|"G X "
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"12"
argument_list|,
name|f
argument_list|,
literal|"G X Y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"13"
argument_list|,
name|f
argument_list|,
literal|"G X Y Z"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"14"
argument_list|,
name|f
argument_list|,
literal|"G Y Z"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"15"
argument_list|,
name|f
argument_list|,
literal|"G Z"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"16"
argument_list|,
name|f
argument_list|,
literal|"G"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test negative base q matching nothing"
argument_list|,
name|req
argument_list|(
literal|"-qlkciyopsbgzyvkylsjhchghjrdf"
argument_list|)
argument_list|,
literal|"//result[@numFound='16']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test negative base q matching something"
argument_list|,
name|req
argument_list|(
literal|"-name:E"
argument_list|)
argument_list|,
literal|"//result[@numFound='13']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test negative base q with two terms"
argument_list|,
name|req
argument_list|(
literal|"-name:G -name:W"
argument_list|)
argument_list|,
literal|"//result[@numFound='7']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test negative base q with three terms"
argument_list|,
name|req
argument_list|(
literal|"-name:G -name:W -name:E"
argument_list|)
argument_list|,
literal|"//result[@numFound='5']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test negative boolean query"
argument_list|,
name|req
argument_list|(
literal|"-(name:G OR name:W)"
argument_list|)
argument_list|,
literal|"//result[@numFound='7']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test non negative q"
argument_list|,
name|req
argument_list|(
literal|"-name:G -name:W -name:E id:[* TO *]"
argument_list|)
argument_list|,
literal|"//result[@numFound='5']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test non negative q"
argument_list|,
name|req
argument_list|(
literal|"-name:G -name:W -name:E +id:[* TO *]"
argument_list|)
argument_list|,
literal|"//result[@numFound='5']"
argument_list|)
expr_stmt|;
comment|// now for the filters...
name|assertQ
argument_list|(
literal|"test negative base q matching nothing, with filters"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"-qlkciyopsbgzyvkylsjhchghjrdf"
argument_list|,
literal|"fq"
argument_list|,
literal|"name:A"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test negative filters"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"name:A"
argument_list|,
literal|"fq"
argument_list|,
literal|"-name:A"
argument_list|)
argument_list|,
literal|"//result[@numFound='0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test negative filters"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"name:A"
argument_list|,
literal|"fq"
argument_list|,
literal|"-name:A"
argument_list|)
argument_list|,
literal|"//result[@numFound='0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test negative filters"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"-name:E"
argument_list|,
literal|"fq"
argument_list|,
literal|"name:E"
argument_list|)
argument_list|,
literal|"//result[@numFound='0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test negative filters"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"-name:E"
argument_list|,
literal|"fq"
argument_list|,
literal|"name:W"
argument_list|)
argument_list|,
literal|"//result[@numFound='2']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test negative filters"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"-name:E"
argument_list|,
literal|"fq"
argument_list|,
literal|"name:W"
argument_list|)
argument_list|,
literal|"//result[@numFound='2']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"one pos filter, one neg"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"-name:E"
argument_list|,
literal|"fq"
argument_list|,
literal|"name:W"
argument_list|,
literal|"fq"
argument_list|,
literal|"-name:G"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"two neg filters"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"-name:E"
argument_list|,
literal|"fq"
argument_list|,
literal|"-name:W"
argument_list|,
literal|"fq"
argument_list|,
literal|"-name:G"
argument_list|)
argument_list|,
literal|"//result[@numFound='5']"
comment|// ABCCD
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"three neg filters"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"-name:E"
argument_list|,
literal|"fq"
argument_list|,
literal|"-name:W"
argument_list|,
literal|"fq"
argument_list|,
literal|"-name:G"
argument_list|,
literal|"fq"
argument_list|,
literal|"-name:C"
argument_list|)
argument_list|,
literal|"//result[@numFound='3']"
comment|// ABD
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"compound neg filters"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"-name:E"
argument_list|,
literal|"fq"
argument_list|,
literal|"-name:W -name:G"
argument_list|,
literal|"fq"
argument_list|,
literal|"-name:C"
argument_list|)
argument_list|,
literal|"//result[@numFound='3']"
comment|// ABD
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"compound neg filters"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"-name:E"
argument_list|,
literal|"fq"
argument_list|,
literal|"-name:W -name:G -name:C"
argument_list|)
argument_list|,
literal|"//result[@numFound='3']"
comment|// ABD
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"compound neg filters"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"-name:E"
argument_list|,
literal|"fq"
argument_list|,
literal|"-(name:W name:G name:C)"
argument_list|)
argument_list|,
literal|"//result[@numFound='3']"
comment|// ABD
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"three neg filters + pos"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"-name:E"
argument_list|,
literal|"fq"
argument_list|,
literal|"-name:W"
argument_list|,
literal|"fq"
argument_list|,
literal|"-name:G"
argument_list|,
literal|"fq"
argument_list|,
literal|"-name:C"
argument_list|,
literal|"fq"
argument_list|,
literal|"name:G"
argument_list|)
argument_list|,
literal|"//result[@numFound='0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"three neg filters + pos"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"-name:E"
argument_list|,
literal|"fq"
argument_list|,
literal|"-name:W"
argument_list|,
literal|"fq"
argument_list|,
literal|"-name:G"
argument_list|,
literal|"fq"
argument_list|,
literal|"-name:C"
argument_list|,
literal|"fq"
argument_list|,
literal|"+id:1"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
comment|// A
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"three neg filters + pos"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"-name:E"
argument_list|,
literal|"fq"
argument_list|,
literal|"-name:W"
argument_list|,
literal|"fq"
argument_list|,
literal|"-name:G"
argument_list|,
literal|"fq"
argument_list|,
literal|"-name:C"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:[* TO *]"
argument_list|)
argument_list|,
literal|"//result[@numFound='3']"
comment|// ABD
argument_list|)
expr_stmt|;
comment|// QueryParser turns term queries on stopwords into a BooleanQuery with
comment|// zero clauses.
name|assertQ
argument_list|(
literal|"neg base query on stopword"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"-text:stopworda"
argument_list|)
argument_list|,
literal|"//result[@numFound='16']"
comment|// ABD
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"negative filter on stopword"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:[* TO *]"
argument_list|,
literal|"fq"
argument_list|,
literal|"-text:stopworda"
argument_list|)
argument_list|,
literal|"//result[@numFound='16']"
comment|// ABD
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"two negative filters on stopword"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:[* TO *]"
argument_list|,
literal|"fq"
argument_list|,
literal|"-text:stopworda"
argument_list|,
literal|"fq"
argument_list|,
literal|"-text:stopworda"
argument_list|)
argument_list|,
literal|"//result[@numFound='16']"
comment|// ABD
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"compound negative filters with stopword"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:[* TO *]"
argument_list|,
literal|"fq"
argument_list|,
literal|"-text:stopworda -id:1"
argument_list|)
argument_list|,
literal|"//result[@numFound='15']"
comment|// ABD
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
