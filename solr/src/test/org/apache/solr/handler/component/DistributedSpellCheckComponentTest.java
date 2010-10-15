begin_unit
begin_package
DECL|package|org.apache.solr.handler.component
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|BaseDistributedSearchTestCase
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
name|client
operator|.
name|solrj
operator|.
name|SolrServer
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
name|util
operator|.
name|AbstractSolrTestCase
import|;
end_import
begin_comment
comment|/**  * Test for SpellCheckComponent's distributed querying  *  * @since solr 1.5  * @version $Id$  * @see org.apache.solr.handler.component.SpellCheckComponent  */
end_comment
begin_class
DECL|class|DistributedSpellCheckComponentTest
specifier|public
class|class
name|DistributedSpellCheckComponentTest
extends|extends
name|BaseDistributedSearchTestCase
block|{
DECL|method|DistributedSpellCheckComponentTest
specifier|public
name|DistributedSpellCheckComponentTest
parameter_list|()
block|{
comment|//fixShardCount=true;
comment|//shardCount=2;
block|}
DECL|field|saveProp
specifier|private
name|String
name|saveProp
decl_stmt|;
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
comment|// this test requires FSDir
name|saveProp
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"solr.directoryFactory"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.directoryFactory"
argument_list|,
literal|"solr.StandardDirectoryFactory"
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|saveProp
operator|==
literal|null
condition|)
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.directoryFactory"
argument_list|)
expr_stmt|;
else|else
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.directoryFactory"
argument_list|,
name|saveProp
argument_list|)
expr_stmt|;
block|}
DECL|method|q
specifier|private
name|void
name|q
parameter_list|(
name|Object
modifier|...
name|q
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
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
name|q
operator|.
name|length
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|params
operator|.
name|add
argument_list|(
name|q
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
argument_list|,
name|q
index|[
name|i
operator|+
literal|1
index|]
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|controlClient
operator|.
name|query
argument_list|(
name|params
argument_list|)
expr_stmt|;
comment|// query a random server
name|params
operator|.
name|set
argument_list|(
literal|"shards"
argument_list|,
name|shards
argument_list|)
expr_stmt|;
name|int
name|which
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|clients
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|SolrServer
name|client
init|=
name|clients
operator|.
name|get
argument_list|(
name|which
argument_list|)
decl_stmt|;
name|client
operator|.
name|query
argument_list|(
name|params
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doTest
specifier|public
name|void
name|doTest
parameter_list|()
throws|throws
name|Exception
block|{
name|del
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"1"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"toyota"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"2"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"chevrolet"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"3"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"suzuki"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"4"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"ford"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"5"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"ferrari"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"6"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"jaguar"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"7"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"mclaren"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"8"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"sonata"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"9"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quick red fox jumped over the lazy brown dogs."
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"10"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"blue"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"12"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"glue"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"13"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"14"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"15"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"16"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"17"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"18"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"19"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"20"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"21"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"22"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"23"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"24"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|handle
operator|.
name|clear
argument_list|()
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"QTime"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"timestamp"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"maxScore"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
comment|// we care only about the spellcheck results
name|handle
operator|.
name|put
argument_list|(
literal|"response"
argument_list|,
name|SKIP
argument_list|)
expr_stmt|;
name|q
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_BUILD
argument_list|,
literal|"true"
argument_list|,
literal|"qt"
argument_list|,
literal|"spellCheckCompRH"
argument_list|,
literal|"shards.qt"
argument_list|,
literal|"spellCheckCompRH"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,lowerfilt"
argument_list|,
literal|"spellcheck.q"
argument_list|,
literal|"toyata"
argument_list|,
literal|"spellcheck"
argument_list|,
literal|"true"
argument_list|,
literal|"qt"
argument_list|,
literal|"spellCheckCompRH"
argument_list|,
literal|"shards.qt"
argument_list|,
literal|"spellCheckCompRH"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,lowerfilt"
argument_list|,
literal|"spellcheck.q"
argument_list|,
literal|"toyata"
argument_list|,
literal|"spellcheck"
argument_list|,
literal|"true"
argument_list|,
literal|"qt"
argument_list|,
literal|"spellCheckCompRH"
argument_list|,
literal|"shards.qt"
argument_list|,
literal|"spellCheckCompRH"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_EXTENDED_RESULTS
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,lowerfilt"
argument_list|,
literal|"spellcheck.q"
argument_list|,
literal|"bluo"
argument_list|,
literal|"spellcheck"
argument_list|,
literal|"true"
argument_list|,
literal|"qt"
argument_list|,
literal|"spellCheckCompRH"
argument_list|,
literal|"shards.qt"
argument_list|,
literal|"spellCheckCompRH"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_EXTENDED_RESULTS
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_COUNT
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"The quick reb fox jumped over the lazy brown dogs"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,lowerfilt"
argument_list|,
literal|"spellcheck"
argument_list|,
literal|"true"
argument_list|,
literal|"qt"
argument_list|,
literal|"spellCheckCompRH"
argument_list|,
literal|"shards.qt"
argument_list|,
literal|"spellCheckCompRH"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_EXTENDED_RESULTS
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_COUNT
argument_list|,
literal|"4"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_COLLATE
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"lowerfilt:(+quock +reb)"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,lowerfilt"
argument_list|,
literal|"spellcheck"
argument_list|,
literal|"true"
argument_list|,
literal|"qt"
argument_list|,
literal|"spellCheckCompRH"
argument_list|,
literal|"shards.qt"
argument_list|,
literal|"spellCheckCompRH"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_EXTENDED_RESULTS
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_COUNT
argument_list|,
literal|"10"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_COLLATE
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_MAX_COLLATION_TRIES
argument_list|,
literal|"10"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_MAX_COLLATIONS
argument_list|,
literal|"10"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_COLLATE_EXTENDED_RESULTS
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"lowerfilt:(+quock +reb)"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,lowerfilt"
argument_list|,
literal|"spellcheck"
argument_list|,
literal|"true"
argument_list|,
literal|"qt"
argument_list|,
literal|"spellCheckCompRH"
argument_list|,
literal|"shards.qt"
argument_list|,
literal|"spellCheckCompRH"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_EXTENDED_RESULTS
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_COUNT
argument_list|,
literal|"10"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_COLLATE
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_MAX_COLLATION_TRIES
argument_list|,
literal|"10"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_MAX_COLLATIONS
argument_list|,
literal|"10"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_COLLATE_EXTENDED_RESULTS
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"lowerfilt:(+quock +reb)"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,lowerfilt"
argument_list|,
literal|"spellcheck"
argument_list|,
literal|"true"
argument_list|,
literal|"qt"
argument_list|,
literal|"spellCheckCompRH"
argument_list|,
literal|"shards.qt"
argument_list|,
literal|"spellCheckCompRH"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_EXTENDED_RESULTS
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_COUNT
argument_list|,
literal|"10"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_COLLATE
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_MAX_COLLATION_TRIES
argument_list|,
literal|"0"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_MAX_COLLATIONS
argument_list|,
literal|"1"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_COLLATE_EXTENDED_RESULTS
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
