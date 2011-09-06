begin_unit
begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
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
name|BytesRef
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
name|core
operator|.
name|SolrCore
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
name|plugin
operator|.
name|PluginInfoInitialized
import|;
end_import
begin_class
DECL|class|UpdateLog
specifier|public
specifier|abstract
class|class
name|UpdateLog
implements|implements
name|PluginInfoInitialized
block|{
DECL|field|ADD
specifier|public
specifier|static
specifier|final
name|int
name|ADD
init|=
literal|0x00
decl_stmt|;
DECL|field|DELETE
specifier|public
specifier|static
specifier|final
name|int
name|DELETE
init|=
literal|0x01
decl_stmt|;
DECL|field|DELETE_BY_QUERY
specifier|public
specifier|static
specifier|final
name|int
name|DELETE_BY_QUERY
init|=
literal|0x02
decl_stmt|;
DECL|method|init
specifier|public
specifier|abstract
name|void
name|init
parameter_list|(
name|UpdateHandler
name|uhandler
parameter_list|,
name|SolrCore
name|core
parameter_list|)
function_decl|;
DECL|method|add
specifier|public
specifier|abstract
name|void
name|add
parameter_list|(
name|AddUpdateCommand
name|cmd
parameter_list|)
function_decl|;
DECL|method|delete
specifier|public
specifier|abstract
name|void
name|delete
parameter_list|(
name|DeleteUpdateCommand
name|cmd
parameter_list|)
function_decl|;
DECL|method|deleteByQuery
specifier|public
specifier|abstract
name|void
name|deleteByQuery
parameter_list|(
name|DeleteUpdateCommand
name|cmd
parameter_list|)
function_decl|;
DECL|method|preCommit
specifier|public
specifier|abstract
name|void
name|preCommit
parameter_list|(
name|CommitUpdateCommand
name|cmd
parameter_list|)
function_decl|;
DECL|method|postCommit
specifier|public
specifier|abstract
name|void
name|postCommit
parameter_list|(
name|CommitUpdateCommand
name|cmd
parameter_list|)
function_decl|;
DECL|method|preSoftCommit
specifier|public
specifier|abstract
name|void
name|preSoftCommit
parameter_list|(
name|CommitUpdateCommand
name|cmd
parameter_list|)
function_decl|;
DECL|method|postSoftCommit
specifier|public
specifier|abstract
name|void
name|postSoftCommit
parameter_list|(
name|CommitUpdateCommand
name|cmd
parameter_list|)
function_decl|;
DECL|method|lookup
specifier|public
specifier|abstract
name|Object
name|lookup
parameter_list|(
name|BytesRef
name|indexedId
parameter_list|)
function_decl|;
DECL|method|close
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
function_decl|;
block|}
end_class
end_unit
