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
name|commons
operator|.
name|lang
operator|.
name|StringUtils
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
name|ArrayList
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
name|Random
import|;
end_import
begin_class
DECL|class|TestPseudoReturnFields
specifier|public
class|class
name|TestPseudoReturnFields
extends|extends
name|SolrTestCaseJ4
block|{
comment|// :TODO: datatypes produced by the functions used may change
comment|/**    * values of the fl param that mean all real fields    */
DECL|field|ALL_REAL_FIELDS
specifier|private
specifier|static
name|String
index|[]
name|ALL_REAL_FIELDS
init|=
operator|new
name|String
index|[]
block|{
literal|""
block|,
literal|"*"
block|}
decl_stmt|;
comment|/**    * values of the fl param that mean all real fields and score    */
DECL|field|SCORE_AND_REAL_FIELDS
specifier|private
specifier|static
name|String
index|[]
name|SCORE_AND_REAL_FIELDS
init|=
operator|new
name|String
index|[]
block|{
literal|"score,*"
block|,
literal|"*,score"
block|}
decl_stmt|;
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
literal|"enable.update.log"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
comment|// schema12 doesn't support _version_
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema12.xml"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"42"
argument_list|,
literal|"val_i"
argument_list|,
literal|"1"
argument_list|,
literal|"ssto"
argument_list|,
literal|"X"
argument_list|,
literal|"subject"
argument_list|,
literal|"aaa"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"43"
argument_list|,
literal|"val_i"
argument_list|,
literal|"9"
argument_list|,
literal|"ssto"
argument_list|,
literal|"X"
argument_list|,
literal|"subject"
argument_list|,
literal|"bbb"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"44"
argument_list|,
literal|"val_i"
argument_list|,
literal|"4"
argument_list|,
literal|"ssto"
argument_list|,
literal|"X"
argument_list|,
literal|"subject"
argument_list|,
literal|"aaa"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"45"
argument_list|,
literal|"val_i"
argument_list|,
literal|"6"
argument_list|,
literal|"ssto"
argument_list|,
literal|"X"
argument_list|,
literal|"subject"
argument_list|,
literal|"aaa"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"46"
argument_list|,
literal|"val_i"
argument_list|,
literal|"3"
argument_list|,
literal|"ssto"
argument_list|,
literal|"X"
argument_list|,
literal|"subject"
argument_list|,
literal|"ggg"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultiValued
specifier|public
name|void
name|testMultiValued
parameter_list|()
throws|throws
name|Exception
block|{
comment|// the response writers used to consult isMultiValued on the field
comment|// but this doesn't work when you alias a single valued field to
comment|// a multi valued field (the field value is copied first, then
comment|// if the type lookup is done again later, we get the wrong thing). SOLR-4036
comment|// score as psuedo field - precondition checks
for|for
control|(
name|String
name|name
range|:
operator|new
name|String
index|[]
block|{
literal|"score"
block|,
literal|"val_ss"
block|}
control|)
block|{
name|SchemaField
name|sf
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getLatestSchema
argument_list|()
operator|.
name|getFieldOrNull
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Test depends on a (dynamic) field mtching '"
operator|+
name|name
operator|+
literal|"', schema was changed out from under us!"
argument_list|,
name|sf
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Test depends on a multivalued dynamic field matching '"
operator|+
name|name
operator|+
literal|"', schema was changed out from under us!"
argument_list|,
name|sf
operator|.
name|multiValued
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// score as psuedo field
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:42"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,score"
argument_list|)
argument_list|,
literal|"/response/docs==[{'id':'42','score':1.0}]"
argument_list|)
expr_stmt|;
comment|// single value int using alias that matches multivalued dynamic field
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:42"
argument_list|,
literal|"fl"
argument_list|,
literal|"val_ss:val_i, val2_ss:10"
argument_list|)
argument_list|,
literal|"/response/docs==[{'val2_ss':10,'val_ss':1}]"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
literal|"/get"
argument_list|,
literal|"id"
argument_list|,
literal|"42"
argument_list|,
literal|"fl"
argument_list|,
literal|"val_ss:val_i, val2_ss:10"
argument_list|)
argument_list|,
literal|"/doc=={'val2_ss':10,'val_ss':1}"
argument_list|)
expr_stmt|;
comment|// also check real-time-get from transaction log
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"42"
argument_list|,
literal|"val_i"
argument_list|,
literal|"1"
argument_list|,
literal|"ssto"
argument_list|,
literal|"X"
argument_list|,
literal|"subject"
argument_list|,
literal|"aaa"
argument_list|)
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
literal|"/get"
argument_list|,
literal|"id"
argument_list|,
literal|"42"
argument_list|,
literal|"fl"
argument_list|,
literal|"val_ss:val_i, val2_ss:10"
argument_list|)
argument_list|,
literal|"/doc=={'val2_ss':10,'val_ss':1}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAllRealFields
specifier|public
name|void
name|testAllRealFields
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|String
name|fl
range|:
name|ALL_REAL_FIELDS
control|)
block|{
name|assertQ
argument_list|(
literal|"fl="
operator|+
name|fl
operator|+
literal|" ... all real fields"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
name|fl
argument_list|)
argument_list|,
literal|"//result[@numFound='5']"
argument_list|,
literal|"//result/doc/str[@name='id']"
argument_list|,
literal|"//result/doc/int[@name='val_i']"
argument_list|,
literal|"//result/doc/str[@name='ssto']"
argument_list|,
literal|"//result/doc/str[@name='subject']"
argument_list|,
literal|"//result/doc[count(*)=4]"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testScoreAndAllRealFields
specifier|public
name|void
name|testScoreAndAllRealFields
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|String
name|fl
range|:
name|SCORE_AND_REAL_FIELDS
control|)
block|{
name|assertQ
argument_list|(
literal|"fl="
operator|+
name|fl
operator|+
literal|" ... score and real fields"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
name|fl
argument_list|)
argument_list|,
literal|"//result[@numFound='5']"
argument_list|,
literal|"//result/doc/str[@name='id']"
argument_list|,
literal|"//result/doc/int[@name='val_i']"
argument_list|,
literal|"//result/doc/str[@name='ssto']"
argument_list|,
literal|"//result/doc/str[@name='subject']"
argument_list|,
literal|"//result/doc/float[@name='score']"
argument_list|,
literal|"//result/doc[count(*)=5]"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testScoreAndExplicitRealFields
specifier|public
name|void
name|testScoreAndExplicitRealFields
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
literal|"fl=score,val_i"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"score,val_i"
argument_list|)
argument_list|,
literal|"//result[@numFound='5']"
argument_list|,
literal|"//result/doc/int[@name='val_i']"
argument_list|,
literal|"//result/doc/float[@name='score']"
argument_list|,
literal|"//result/doc[count(*)=2]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"fl=score&fl=val_i"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"score"
argument_list|,
literal|"fl"
argument_list|,
literal|"val_i"
argument_list|)
argument_list|,
literal|"//result[@numFound='5']"
argument_list|,
literal|"//result/doc/int[@name='val_i']"
argument_list|,
literal|"//result/doc/float[@name='score']"
argument_list|,
literal|"//result/doc[count(*)=2]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"fl=val_i"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"val_i"
argument_list|)
argument_list|,
literal|"//result[@numFound='5']"
argument_list|,
literal|"//result/doc/int[@name='val_i']"
argument_list|,
literal|"//result/doc[count(*)=1]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFunctions
specifier|public
name|void
name|testFunctions
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
literal|"fl=log(val_i)"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"log(val_i)"
argument_list|)
argument_list|,
literal|"//result[@numFound='5']"
argument_list|,
literal|"//result/doc/double[@name='log(val_i)']"
argument_list|,
literal|"//result/doc[count(*)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"fl=log(val_i),abs(val_i)"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"log(val_i),abs(val_i)"
argument_list|)
argument_list|,
literal|"//result[@numFound='5']"
argument_list|,
literal|"//result/doc/double[@name='log(val_i)']"
argument_list|,
literal|"//result/doc/float[@name='abs(val_i)']"
argument_list|,
literal|"//result/doc[count(*)=2]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"fl=log(val_i)&fl=abs(val_i)"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"log(val_i)"
argument_list|,
literal|"fl"
argument_list|,
literal|"abs(val_i)"
argument_list|)
argument_list|,
literal|"//result[@numFound='5']"
argument_list|,
literal|"//result/doc/double[@name='log(val_i)']"
argument_list|,
literal|"//result/doc/float[@name='abs(val_i)']"
argument_list|,
literal|"//result/doc[count(*)=2]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFunctionsAndExplicit
specifier|public
name|void
name|testFunctionsAndExplicit
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
literal|"fl=log(val_i),val_i"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"log(val_i),val_i"
argument_list|)
argument_list|,
literal|"//result[@numFound='5']"
argument_list|,
literal|"//result/doc/double[@name='log(val_i)']"
argument_list|,
literal|"//result/doc/int[@name='val_i']"
argument_list|,
literal|"//result/doc[count(*)=2]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"fl=log(val_i)&fl=val_i"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"log(val_i)"
argument_list|,
literal|"fl"
argument_list|,
literal|"val_i"
argument_list|)
argument_list|,
literal|"//result[@numFound='5']"
argument_list|,
literal|"//result/doc/double[@name='log(val_i)']"
argument_list|,
literal|"//result/doc/int[@name='val_i']"
argument_list|,
literal|"//result/doc[count(*)=2]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFunctionsAndScore
specifier|public
name|void
name|testFunctionsAndScore
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
literal|"fl=log(val_i),score"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"log(val_i),score"
argument_list|)
argument_list|,
literal|"//result[@numFound='5']"
argument_list|,
literal|"//result/doc/float[@name='score']"
argument_list|,
literal|"//result/doc/double[@name='log(val_i)']"
argument_list|,
literal|"//result/doc[count(*)=2]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"fl=log(val_i)&fl=score"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"log(val_i)"
argument_list|,
literal|"fl"
argument_list|,
literal|"score"
argument_list|)
argument_list|,
literal|"//result[@numFound='5']"
argument_list|,
literal|"//result/doc/float[@name='score']"
argument_list|,
literal|"//result/doc/double[@name='log(val_i)']"
argument_list|,
literal|"//result/doc[count(*)=2]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"fl=score,log(val_i),abs(val_i)"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"score,log(val_i),abs(val_i)"
argument_list|)
argument_list|,
literal|"//result[@numFound='5']"
argument_list|,
literal|"//result/doc/float[@name='score']"
argument_list|,
literal|"//result/doc/double[@name='log(val_i)']"
argument_list|,
literal|"//result/doc/float[@name='abs(val_i)']"
argument_list|,
literal|"//result/doc[count(*)=3]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"fl=score&fl=log(val_i)&fl=abs(val_i)"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"score"
argument_list|,
literal|"fl"
argument_list|,
literal|"log(val_i)"
argument_list|,
literal|"fl"
argument_list|,
literal|"abs(val_i)"
argument_list|)
argument_list|,
literal|"//result[@numFound='5']"
argument_list|,
literal|"//result/doc/float[@name='score']"
argument_list|,
literal|"//result/doc/double[@name='log(val_i)']"
argument_list|,
literal|"//result/doc/float[@name='abs(val_i)']"
argument_list|,
literal|"//result/doc[count(*)=3]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGlobs
specifier|public
name|void
name|testGlobs
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
literal|"fl=val_*"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"val_*"
argument_list|)
argument_list|,
literal|"//result[@numFound='5']"
argument_list|,
literal|"//result/doc/int[@name='val_i']"
argument_list|,
literal|"//result/doc[count(*)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"fl=val_*,subj*"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"val_*,subj*"
argument_list|)
argument_list|,
literal|"//result[@numFound='5']"
argument_list|,
literal|"//result/doc/int[@name='val_i']"
argument_list|,
literal|"//result/doc/str[@name='subject']"
argument_list|,
literal|"//result/doc[count(*)=2]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"fl=val_*&fl=subj*"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"val_*"
argument_list|,
literal|"fl"
argument_list|,
literal|"subj*"
argument_list|)
argument_list|,
literal|"//result[@numFound='5']"
argument_list|,
literal|"//result/doc/int[@name='val_i']"
argument_list|,
literal|"//result/doc/str[@name='subject']"
argument_list|,
literal|"//result/doc[count(*)=2]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGlobsAndExplicit
specifier|public
name|void
name|testGlobsAndExplicit
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
literal|"fl=val_*,id"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"val_*,id"
argument_list|)
argument_list|,
literal|"//result[@numFound='5']"
argument_list|,
literal|"//result/doc/int[@name='val_i']"
argument_list|,
literal|"//result/doc/str[@name='id']"
argument_list|,
literal|"//result/doc[count(*)=2]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"fl=val_*,subj*,id"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"val_*,subj*,id"
argument_list|)
argument_list|,
literal|"//result[@numFound='5']"
argument_list|,
literal|"//result/doc/int[@name='val_i']"
argument_list|,
literal|"//result/doc/str[@name='subject']"
argument_list|,
literal|"//result/doc/str[@name='id']"
argument_list|,
literal|"//result/doc[count(*)=3]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"fl=val_*&fl=subj*&fl=id"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"val_*"
argument_list|,
literal|"fl"
argument_list|,
literal|"subj*"
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|)
argument_list|,
literal|"//result[@numFound='5']"
argument_list|,
literal|"//result/doc/int[@name='val_i']"
argument_list|,
literal|"//result/doc/str[@name='subject']"
argument_list|,
literal|"//result/doc/str[@name='id']"
argument_list|,
literal|"//result/doc[count(*)=3]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGlobsAndScore
specifier|public
name|void
name|testGlobsAndScore
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
literal|"fl=val_*,score"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"val_*,score"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//result[@numFound='5']"
argument_list|,
literal|"//result/doc/float[@name='score']"
argument_list|,
literal|"//result/doc/int[@name='val_i']"
argument_list|,
literal|"//result/doc[count(*)=2]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"fl=val_*,subj*,score"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"val_*,subj*,score"
argument_list|)
argument_list|,
literal|"//result[@numFound='5']"
argument_list|,
literal|"//result/doc/float[@name='score']"
argument_list|,
literal|"//result/doc/int[@name='val_i']"
argument_list|,
literal|"//result/doc/str[@name='subject']"
argument_list|,
literal|"//result/doc[count(*)=3]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"fl=val_*&fl=subj*&fl=score"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"val_*"
argument_list|,
literal|"fl"
argument_list|,
literal|"subj*"
argument_list|,
literal|"fl"
argument_list|,
literal|"score"
argument_list|)
argument_list|,
literal|"//result[@numFound='5']"
argument_list|,
literal|"//result/doc/float[@name='score']"
argument_list|,
literal|"//result/doc/int[@name='val_i']"
argument_list|,
literal|"//result/doc/str[@name='subject']"
argument_list|,
literal|"//result/doc[count(*)=3]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAugmenters
specifier|public
name|void
name|testAugmenters
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
literal|"fl=[docid]"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"[docid]"
argument_list|)
argument_list|,
literal|"//result[@numFound='5']"
argument_list|,
literal|"//result/doc/int[@name='[docid]']"
argument_list|,
literal|"//result/doc[count(*)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"fl=[docid],[explain]"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"[docid],[explain]"
argument_list|)
argument_list|,
literal|"//result[@numFound='5']"
argument_list|,
literal|"//result/doc/int[@name='[docid]']"
argument_list|,
literal|"//result/doc/str[@name='[explain]']"
argument_list|,
literal|"//result/doc[count(*)=2]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"fl=[docid]&fl=[explain]"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"[docid]"
argument_list|,
literal|"fl"
argument_list|,
literal|"[explain]"
argument_list|)
argument_list|,
literal|"//result[@numFound='5']"
argument_list|,
literal|"//result/doc/int[@name='[docid]']"
argument_list|,
literal|"//result/doc/str[@name='[explain]']"
argument_list|,
literal|"//result/doc[count(*)=2]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAugmentersAndExplicit
specifier|public
name|void
name|testAugmentersAndExplicit
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
literal|"fl=[docid],id"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"[docid],id"
argument_list|)
argument_list|,
literal|"//result[@numFound='5']"
argument_list|,
literal|"//result/doc/int[@name='[docid]']"
argument_list|,
literal|"//result/doc/str[@name='id']"
argument_list|,
literal|"//result/doc[count(*)=2]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"fl=[docid],[explain],id"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"[docid],[explain],id"
argument_list|)
argument_list|,
literal|"//result[@numFound='5']"
argument_list|,
literal|"//result/doc/int[@name='[docid]']"
argument_list|,
literal|"//result/doc/str[@name='[explain]']"
argument_list|,
literal|"//result/doc/str[@name='id']"
argument_list|,
literal|"//result/doc[count(*)=3]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"fl=[docid]&fl=[explain]&fl=id"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"[docid]"
argument_list|,
literal|"fl"
argument_list|,
literal|"[explain]"
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|)
argument_list|,
literal|"//result[@numFound='5']"
argument_list|,
literal|"//result/doc/int[@name='[docid]']"
argument_list|,
literal|"//result/doc/str[@name='[explain]']"
argument_list|,
literal|"//result/doc/str[@name='id']"
argument_list|,
literal|"//result/doc[count(*)=3]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAugmentersAndScore
specifier|public
name|void
name|testAugmentersAndScore
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
literal|"fl=[docid],score"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"[docid],score"
argument_list|)
argument_list|,
literal|"//result[@numFound='5']"
argument_list|,
literal|"//result/doc/float[@name='score']"
argument_list|,
literal|"//result/doc/int[@name='[docid]']"
argument_list|,
literal|"//result/doc[count(*)=2]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"fl=[docid],[explain],score"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"[docid],[explain],score"
argument_list|)
argument_list|,
literal|"//result[@numFound='5']"
argument_list|,
literal|"//result/doc/float[@name='score']"
argument_list|,
literal|"//result/doc/int[@name='[docid]']"
argument_list|,
literal|"//result/doc/str[@name='[explain]']"
argument_list|,
literal|"//result/doc[count(*)=3]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"fl=[docid]&fl=[explain]&fl=score"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"[docid]"
argument_list|,
literal|"fl"
argument_list|,
literal|"[explain]"
argument_list|,
literal|"fl"
argument_list|,
literal|"score"
argument_list|)
argument_list|,
literal|"//result[@numFound='5']"
argument_list|,
literal|"//result/doc/float[@name='score']"
argument_list|,
literal|"//result/doc/int[@name='[docid]']"
argument_list|,
literal|"//result/doc/str[@name='[explain]']"
argument_list|,
literal|"//result/doc[count(*)=3]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAugmentersGlobsExplicitAndScoreOhMy
specifier|public
name|void
name|testAugmentersGlobsExplicitAndScoreOhMy
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
comment|// NOTE: 'ssto' is the missing one
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|fl
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"id"
argument_list|,
literal|"[docid]"
argument_list|,
literal|"[explain]"
argument_list|,
literal|"score"
argument_list|,
literal|"val_*"
argument_list|,
literal|"subj*"
argument_list|)
decl_stmt|;
specifier|final
name|int
name|iters
init|=
name|atLeast
argument_list|(
name|random
argument_list|,
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
name|iters
condition|;
name|i
operator|++
control|)
block|{
name|Collections
operator|.
name|shuffle
argument_list|(
name|fl
argument_list|,
name|random
argument_list|)
expr_stmt|;
specifier|final
name|String
name|singleFl
init|=
name|StringUtils
operator|.
name|join
argument_list|(
name|fl
operator|.
name|toArray
argument_list|()
argument_list|,
literal|','
argument_list|)
decl_stmt|;
name|assertQ
argument_list|(
literal|"fl="
operator|+
name|singleFl
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
name|singleFl
argument_list|)
argument_list|,
literal|"//result[@numFound='5']"
argument_list|,
literal|"//result/doc/str[@name='id']"
argument_list|,
literal|"//result/doc/float[@name='score']"
argument_list|,
literal|"//result/doc/str[@name='subject']"
argument_list|,
literal|"//result/doc/int[@name='val_i']"
argument_list|,
literal|"//result/doc/int[@name='[docid]']"
argument_list|,
literal|"//result/doc/str[@name='[explain]']"
argument_list|,
literal|"//result/doc[count(*)=6]"
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|params
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
operator|(
name|fl
operator|.
name|size
argument_list|()
operator|*
literal|2
operator|)
operator|+
literal|4
argument_list|)
decl_stmt|;
specifier|final
name|StringBuilder
name|info
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|params
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|item
range|:
name|fl
control|)
block|{
name|params
operator|.
name|add
argument_list|(
literal|"fl"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
name|info
operator|.
name|append
argument_list|(
literal|"&fl="
argument_list|)
operator|.
name|append
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
name|assertQ
argument_list|(
name|info
operator|.
name|toString
argument_list|()
argument_list|,
name|req
argument_list|(
operator|(
name|String
index|[]
operator|)
name|params
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
argument_list|,
literal|"//result[@numFound='5']"
argument_list|,
literal|"//result/doc/str[@name='id']"
argument_list|,
literal|"//result/doc/float[@name='score']"
argument_list|,
literal|"//result/doc/str[@name='subject']"
argument_list|,
literal|"//result/doc/int[@name='val_i']"
argument_list|,
literal|"//result/doc/int[@name='[docid]']"
argument_list|,
literal|"//result/doc/str[@name='[explain]']"
argument_list|,
literal|"//result/doc[count(*)=6]"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
