begin_unit
begin_package
DECL|package|org.apache.solr.request
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
package|;
end_package
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletRequest
import|;
end_import
begin_comment
comment|/**  * @author yonik  * @version $Id$  */
end_comment
begin_class
DECL|class|ServletSolrParams
specifier|public
class|class
name|ServletSolrParams
extends|extends
name|MultiMapSolrParams
block|{
DECL|method|ServletSolrParams
specifier|public
name|ServletSolrParams
parameter_list|(
name|ServletRequest
name|req
parameter_list|)
block|{
name|super
argument_list|(
name|req
operator|.
name|getParameterMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|get
specifier|public
name|String
name|get
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|String
index|[]
name|arr
init|=
name|map
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|arr
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|String
name|s
init|=
name|arr
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|s
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|null
return|;
comment|// screen out blank parameters
return|return
name|s
return|;
block|}
block|}
end_class
end_unit
