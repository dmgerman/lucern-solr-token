begin_unit
begin_package
DECL|package|org.apache.solr.spelling.suggest
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
operator|.
name|suggest
package|;
end_package
begin_class
DECL|class|SuggesterFSTTest
specifier|public
class|class
name|SuggesterFSTTest
extends|extends
name|SuggesterTest
block|{
DECL|method|SuggesterFSTTest
specifier|public
name|SuggesterFSTTest
parameter_list|()
block|{
name|super
operator|.
name|requestUri
operator|=
literal|"/suggest_fst"
expr_stmt|;
block|}
block|}
end_class
end_unit
