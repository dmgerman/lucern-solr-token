begin_unit
begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
begin_class
DECL|class|UUIDFieldTest
specifier|public
class|class
name|UUIDFieldTest
extends|extends
name|TestCase
block|{
DECL|method|testToInternal
specifier|public
name|void
name|testToInternal
parameter_list|()
block|{
name|boolean
name|ok
init|=
literal|false
decl_stmt|;
name|UUIDField
name|uuidfield
init|=
operator|new
name|UUIDField
argument_list|()
decl_stmt|;
try|try
block|{
name|uuidfield
operator|.
name|toInternal
argument_list|(
operator|(
name|String
operator|)
literal|null
argument_list|)
expr_stmt|;
name|ok
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|se
parameter_list|)
block|{
name|ok
operator|=
literal|false
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"ID generation from null failed"
argument_list|,
name|ok
argument_list|)
expr_stmt|;
try|try
block|{
name|uuidfield
operator|.
name|toInternal
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|ok
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|se
parameter_list|)
block|{
name|ok
operator|=
literal|false
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"ID generation from empty string failed"
argument_list|,
name|ok
argument_list|)
expr_stmt|;
try|try
block|{
name|uuidfield
operator|.
name|toInternal
argument_list|(
literal|"NEW"
argument_list|)
expr_stmt|;
name|ok
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|se
parameter_list|)
block|{
name|ok
operator|=
literal|false
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"ID generation from 'NEW' failed"
argument_list|,
name|ok
argument_list|)
expr_stmt|;
try|try
block|{
name|uuidfield
operator|.
name|toInternal
argument_list|(
literal|"d574fb6a-5f79-4974-b01a-fcd598a19ef5"
argument_list|)
expr_stmt|;
name|ok
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|se
parameter_list|)
block|{
name|ok
operator|=
literal|false
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"ID generation from UUID failed"
argument_list|,
name|ok
argument_list|)
expr_stmt|;
try|try
block|{
name|uuidfield
operator|.
name|toInternal
argument_list|(
literal|"This is a test"
argument_list|)
expr_stmt|;
name|ok
operator|=
literal|false
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|se
parameter_list|)
block|{
name|ok
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Bad UUID check failed"
argument_list|,
name|ok
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
