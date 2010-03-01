begin_unit
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
name|search
operator|.
name|Sort
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
name|SortField
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
name|util
operator|.
name|AbstractSolrTestCase
import|;
end_import
begin_comment
comment|/**  *  *  **/
end_comment
begin_class
DECL|class|QueryParsingTest
specifier|public
class|class
name|QueryParsingTest
extends|extends
name|AbstractSolrTestCase
block|{
DECL|method|getSchemaFile
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"schema.xml"
return|;
block|}
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solrconfig.xml"
return|;
block|}
DECL|method|testSort
specifier|public
name|void
name|testSort
parameter_list|()
throws|throws
name|Exception
block|{
name|Sort
name|sort
decl_stmt|;
name|IndexSchema
name|schema
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getSchema
argument_list|()
decl_stmt|;
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|"score desc"
argument_list|,
name|schema
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"sort"
argument_list|,
name|sort
argument_list|)
expr_stmt|;
comment|//only 1 thing in the list, no Sort specified
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|"score asc"
argument_list|,
name|schema
argument_list|)
expr_stmt|;
name|SortField
index|[]
name|flds
init|=
name|sort
operator|.
name|getSort
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|SortField
operator|.
name|SCORE
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getReverse
argument_list|()
argument_list|)
expr_stmt|;
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|"weight desc"
argument_list|,
name|schema
argument_list|)
expr_stmt|;
name|flds
operator|=
name|sort
operator|.
name|getSort
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|SortField
operator|.
name|FLOAT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getField
argument_list|()
argument_list|,
literal|"weight"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getReverse
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|"weight desc,bday asc"
argument_list|,
name|schema
argument_list|)
expr_stmt|;
name|flds
operator|=
name|sort
operator|.
name|getSort
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|SortField
operator|.
name|FLOAT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getField
argument_list|()
argument_list|,
literal|"weight"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getReverse
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|1
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|SortField
operator|.
name|LONG
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|1
index|]
operator|.
name|getField
argument_list|()
argument_list|,
literal|"bday"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|1
index|]
operator|.
name|getReverse
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|//order aliases
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|"weight top,bday asc"
argument_list|,
name|schema
argument_list|)
expr_stmt|;
name|flds
operator|=
name|sort
operator|.
name|getSort
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|SortField
operator|.
name|FLOAT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getField
argument_list|()
argument_list|,
literal|"weight"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getReverse
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|1
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|SortField
operator|.
name|LONG
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|1
index|]
operator|.
name|getField
argument_list|()
argument_list|,
literal|"bday"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|1
index|]
operator|.
name|getReverse
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|"weight top,bday bottom"
argument_list|,
name|schema
argument_list|)
expr_stmt|;
name|flds
operator|=
name|sort
operator|.
name|getSort
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|SortField
operator|.
name|FLOAT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getField
argument_list|()
argument_list|,
literal|"weight"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getReverse
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|1
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|SortField
operator|.
name|LONG
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|1
index|]
operator|.
name|getField
argument_list|()
argument_list|,
literal|"bday"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|1
index|]
operator|.
name|getReverse
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|//test weird spacing
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|"weight         desc,            bday         asc"
argument_list|,
name|schema
argument_list|)
expr_stmt|;
name|flds
operator|=
name|sort
operator|.
name|getSort
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|SortField
operator|.
name|FLOAT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getField
argument_list|()
argument_list|,
literal|"weight"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|1
index|]
operator|.
name|getField
argument_list|()
argument_list|,
literal|"bday"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|1
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|SortField
operator|.
name|LONG
argument_list|)
expr_stmt|;
comment|//handles trailing commas
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|"weight desc,"
argument_list|,
name|schema
argument_list|)
expr_stmt|;
name|flds
operator|=
name|sort
operator|.
name|getSort
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|SortField
operator|.
name|FLOAT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getField
argument_list|()
argument_list|,
literal|"weight"
argument_list|)
expr_stmt|;
comment|//test functions
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|"pow(weight, 2) desc"
argument_list|,
name|schema
argument_list|)
expr_stmt|;
name|flds
operator|=
name|sort
operator|.
name|getSort
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|SortField
operator|.
name|CUSTOM
argument_list|)
expr_stmt|;
comment|//Not thrilled about the fragility of string matching here, but...
comment|//the value sources get wrapped, so the out field is different than the input
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getField
argument_list|()
argument_list|,
literal|"pow(float(weight),const(2.0))"
argument_list|)
expr_stmt|;
comment|//test functions (more deep)
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|"sum(product(r_f,sum(d_f,t_f,1)),a_f) asc"
argument_list|,
name|schema
argument_list|)
expr_stmt|;
name|flds
operator|=
name|sort
operator|.
name|getSort
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|SortField
operator|.
name|CUSTOM
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getField
argument_list|()
argument_list|,
literal|"sum(product(float(r_f),sum(float(d_f),float(t_f),const(1.0))),float(a_f))"
argument_list|)
expr_stmt|;
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|"pow(weight,                 2)         desc"
argument_list|,
name|schema
argument_list|)
expr_stmt|;
name|flds
operator|=
name|sort
operator|.
name|getSort
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|SortField
operator|.
name|CUSTOM
argument_list|)
expr_stmt|;
comment|//Not thrilled about the fragility of string matching here, but...
comment|//the value sources get wrapped, so the out field is different than the input
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getField
argument_list|()
argument_list|,
literal|"pow(float(weight),const(2.0))"
argument_list|)
expr_stmt|;
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|"pow(weight, 2) desc, weight    desc,   bday    asc"
argument_list|,
name|schema
argument_list|)
expr_stmt|;
name|flds
operator|=
name|sort
operator|.
name|getSort
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|SortField
operator|.
name|CUSTOM
argument_list|)
expr_stmt|;
comment|//Not thrilled about the fragility of string matching here, but...
comment|//the value sources get wrapped, so the out field is different than the input
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getField
argument_list|()
argument_list|,
literal|"pow(float(weight),const(2.0))"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|1
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|SortField
operator|.
name|FLOAT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|1
index|]
operator|.
name|getField
argument_list|()
argument_list|,
literal|"weight"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|2
index|]
operator|.
name|getField
argument_list|()
argument_list|,
literal|"bday"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|2
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|SortField
operator|.
name|LONG
argument_list|)
expr_stmt|;
comment|//handles trailing commas
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|"weight desc,"
argument_list|,
name|schema
argument_list|)
expr_stmt|;
name|flds
operator|=
name|sort
operator|.
name|getSort
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|SortField
operator|.
name|FLOAT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getField
argument_list|()
argument_list|,
literal|"weight"
argument_list|)
expr_stmt|;
try|try
block|{
comment|//bad number of parens, but the function parser can handle an extra close
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|"pow(weight,2)) desc, bday asc"
argument_list|,
name|schema
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|//Test literals in functions
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|"strdist(foo_s, \"junk\", jw) desc"
argument_list|,
name|schema
argument_list|)
expr_stmt|;
name|flds
operator|=
name|sort
operator|.
name|getSort
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|SortField
operator|.
name|CUSTOM
argument_list|)
expr_stmt|;
comment|//the value sources get wrapped, so the out field is different than the input
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getField
argument_list|()
argument_list|,
literal|"strdist(str(foo_s),literal(junk), dist=org.apache.lucene.search.spell.JaroWinklerDistance)"
argument_list|)
expr_stmt|;
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|""
argument_list|,
name|schema
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|sort
argument_list|)
expr_stmt|;
block|}
DECL|method|testBad
specifier|public
name|void
name|testBad
parameter_list|()
throws|throws
name|Exception
block|{
name|Sort
name|sort
decl_stmt|;
name|IndexSchema
name|schema
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getSchema
argument_list|()
decl_stmt|;
comment|//test some bad vals
try|try
block|{
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|"weight, desc"
argument_list|,
name|schema
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
comment|//expected
block|}
try|try
block|{
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|"w"
argument_list|,
name|schema
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
comment|//expected
block|}
try|try
block|{
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|"weight desc, bday"
argument_list|,
name|schema
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{     }
try|try
block|{
comment|//bad number of commas
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|"pow(weight,,2) desc, bday asc"
argument_list|,
name|schema
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{     }
try|try
block|{
comment|//bad function
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|"pow() desc, bday asc"
argument_list|,
name|schema
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{     }
try|try
block|{
comment|//bad number of parens
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|"pow((weight,2) desc, bday asc"
argument_list|,
name|schema
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{     }
block|}
block|}
end_class
end_unit
