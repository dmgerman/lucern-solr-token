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
name|core
operator|.
name|WhitespaceTokenizer
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
name|synonym
operator|.
name|SynonymFilter
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
name|synonym
operator|.
name|SynonymMap
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
begin_comment
comment|/**  * @since solr 1.4  */
end_comment
begin_class
DECL|class|TestMultiWordSynonyms
specifier|public
class|class
name|TestMultiWordSynonyms
extends|extends
name|BaseTokenTestCase
block|{
annotation|@
name|Test
DECL|method|testMultiWordSynonyms
specifier|public
name|void
name|testMultiWordSynonyms
parameter_list|()
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|rules
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|rules
operator|.
name|add
argument_list|(
literal|"a b c,d"
argument_list|)
expr_stmt|;
name|SynonymMap
name|synMap
init|=
operator|new
name|SynonymMap
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|SynonymFilterFactory
operator|.
name|parseRules
argument_list|(
name|rules
argument_list|,
name|synMap
argument_list|,
literal|"=>"
argument_list|,
literal|","
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|SynonymFilter
name|ts
init|=
operator|new
name|SynonymFilter
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|DEFAULT_VERSION
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"a e"
argument_list|)
argument_list|)
argument_list|,
name|synMap
argument_list|)
decl_stmt|;
comment|// This fails because ["e","e"] is the value of the token stream
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"e"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
