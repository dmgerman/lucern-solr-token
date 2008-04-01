begin_unit
begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
package|;
end_package
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|net
operator|.
name|sf
operator|.
name|snowball
operator|.
name|ext
operator|.
name|EnglishStemmer
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
name|ResourceLoader
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
name|InputStream
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
name|Collections
import|;
end_import
begin_class
DECL|class|EnglishPorterFilterFactoryTest
specifier|public
class|class
name|EnglishPorterFilterFactoryTest
extends|extends
name|BaseTokenTestCase
block|{
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|IOException
block|{
name|EnglishStemmer
name|stemmer
init|=
operator|new
name|net
operator|.
name|sf
operator|.
name|snowball
operator|.
name|ext
operator|.
name|EnglishStemmer
argument_list|()
decl_stmt|;
name|String
index|[]
name|test
init|=
block|{
literal|"The"
block|,
literal|"fledgling"
block|,
literal|"banks"
block|,
literal|"were"
block|,
literal|"counting"
block|,
literal|"on"
block|,
literal|"a"
block|,
literal|"big"
block|,
literal|"boom"
block|,
literal|"in"
block|,
literal|"banking"
block|}
decl_stmt|;
name|StringBuilder
name|gold
init|=
operator|new
name|StringBuilder
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
name|test
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|stemmer
operator|.
name|setCurrent
argument_list|(
name|test
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|stemmer
operator|.
name|stem
argument_list|()
expr_stmt|;
name|gold
operator|.
name|append
argument_list|(
name|stemmer
operator|.
name|getCurrent
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|EnglishPorterFilterFactory
name|factory
init|=
operator|new
name|EnglishPorterFilterFactory
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|factory
operator|.
name|inform
argument_list|(
operator|new
name|LinesMockSolrResourceLoader
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|out
init|=
name|tsToString
argument_list|(
name|factory
operator|.
name|create
argument_list|(
operator|new
name|IterTokenStream
argument_list|(
name|test
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|gold
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
DECL|method|testProtected
specifier|public
name|void
name|testProtected
parameter_list|()
throws|throws
name|Exception
block|{
name|EnglishStemmer
name|stemmer
init|=
operator|new
name|net
operator|.
name|sf
operator|.
name|snowball
operator|.
name|ext
operator|.
name|EnglishStemmer
argument_list|()
decl_stmt|;
name|String
index|[]
name|test
init|=
block|{
literal|"The"
block|,
literal|"fledgling"
block|,
literal|"banks"
block|,
literal|"were"
block|,
literal|"counting"
block|,
literal|"on"
block|,
literal|"a"
block|,
literal|"big"
block|,
literal|"boom"
block|,
literal|"in"
block|,
literal|"banking"
block|}
decl_stmt|;
name|StringBuilder
name|gold
init|=
operator|new
name|StringBuilder
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
name|test
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|test
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"fledgling"
argument_list|)
operator|==
literal|false
operator|&&
name|test
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"banks"
argument_list|)
operator|==
literal|false
condition|)
block|{
name|stemmer
operator|.
name|setCurrent
argument_list|(
name|test
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|stemmer
operator|.
name|stem
argument_list|()
expr_stmt|;
name|gold
operator|.
name|append
argument_list|(
name|stemmer
operator|.
name|getCurrent
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|gold
operator|.
name|append
argument_list|(
name|test
index|[
name|i
index|]
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
block|}
name|EnglishPorterFilterFactory
name|factory
init|=
operator|new
name|EnglishPorterFilterFactory
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|EnglishPorterFilterFactory
operator|.
name|PROTECTED_TOKENS
argument_list|,
literal|"who-cares.txt"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|lines
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Collections
operator|.
name|addAll
argument_list|(
name|lines
argument_list|,
literal|"banks"
argument_list|,
literal|"fledgling"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|inform
argument_list|(
operator|new
name|LinesMockSolrResourceLoader
argument_list|(
name|lines
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|out
init|=
name|tsToString
argument_list|(
name|factory
operator|.
name|create
argument_list|(
operator|new
name|IterTokenStream
argument_list|(
name|test
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|gold
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
DECL|class|LinesMockSolrResourceLoader
class|class
name|LinesMockSolrResourceLoader
implements|implements
name|ResourceLoader
block|{
DECL|field|lines
name|List
argument_list|<
name|String
argument_list|>
name|lines
decl_stmt|;
DECL|method|LinesMockSolrResourceLoader
name|LinesMockSolrResourceLoader
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|lines
parameter_list|)
block|{
name|this
operator|.
name|lines
operator|=
name|lines
expr_stmt|;
block|}
DECL|method|getLines
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getLines
parameter_list|(
name|String
name|resource
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|lines
return|;
block|}
DECL|method|newInstance
specifier|public
name|Object
name|newInstance
parameter_list|(
name|String
name|cname
parameter_list|,
name|String
modifier|...
name|subpackages
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
DECL|method|openResource
specifier|public
name|InputStream
name|openResource
parameter_list|(
name|String
name|resource
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class
end_unit
