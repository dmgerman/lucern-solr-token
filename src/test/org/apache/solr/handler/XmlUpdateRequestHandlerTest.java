begin_unit
begin_package
DECL|package|org.apache.solr.handler
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
package|;
end_package
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
name|Collection
import|;
end_import
begin_import
import|import
name|javanet
operator|.
name|staxutils
operator|.
name|BaseXMLInputFactory
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLInputFactory
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamReader
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
name|SolrInputDocument
import|;
end_import
begin_class
DECL|class|XmlUpdateRequestHandlerTest
specifier|public
class|class
name|XmlUpdateRequestHandlerTest
extends|extends
name|TestCase
block|{
DECL|field|inputFactory
specifier|private
name|XMLInputFactory
name|inputFactory
init|=
name|BaseXMLInputFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
DECL|field|handler
specifier|protected
name|StaxUpdateRequestHandler
name|handler
init|=
operator|new
name|StaxUpdateRequestHandler
argument_list|()
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
DECL|method|testReadDoc
specifier|public
name|void
name|testReadDoc
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|xml
init|=
literal|"<doc boost=\"5.5\">"
operator|+
literal|"<field name=\"id\" boost=\"2.2\">12345</field>"
operator|+
literal|"<field name=\"name\">kitten</field>"
operator|+
literal|"<field name=\"cat\" boost=\"3\">aaa</field>"
operator|+
literal|"<field name=\"cat\" boost=\"4\">bbb</field>"
operator|+
literal|"<field name=\"cat\" boost=\"5\">bbb</field>"
operator|+
literal|"<field name=\"ab\">a&amp;b</field>"
operator|+
literal|"</doc>"
decl_stmt|;
name|XMLStreamReader
name|parser
init|=
name|inputFactory
operator|.
name|createXMLStreamReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|xml
argument_list|)
argument_list|)
decl_stmt|;
name|parser
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// read the START document...
name|SolrInputDocument
name|doc
init|=
name|handler
operator|.
name|readDoc
argument_list|(
name|parser
argument_list|)
decl_stmt|;
comment|// Read boosts
name|assertEquals
argument_list|(
operator|new
name|Float
argument_list|(
literal|5.5f
argument_list|)
argument_list|,
name|doc
operator|.
name|getBoost
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|doc
operator|.
name|getBoost
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Float
argument_list|(
literal|2.2f
argument_list|)
argument_list|,
name|doc
operator|.
name|getBoost
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|doc
operator|.
name|getBoost
argument_list|(
literal|"ab"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Boost is the product of each value
name|assertEquals
argument_list|(
operator|new
name|Float
argument_list|(
literal|3
operator|*
literal|4
operator|*
literal|5
argument_list|)
argument_list|,
name|doc
operator|.
name|getBoost
argument_list|(
literal|"cat"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Read values
name|assertEquals
argument_list|(
literal|"12345"
argument_list|,
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"kitten"
argument_list|,
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a&b"
argument_list|,
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"ab"
argument_list|)
argument_list|)
expr_stmt|;
comment|// read something with escaped characters
name|Collection
argument_list|<
name|Object
argument_list|>
name|out
init|=
name|doc
operator|.
name|getFieldValues
argument_list|(
literal|"cat"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|out
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[aaa, bbb, bbb]"
argument_list|,
name|out
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
