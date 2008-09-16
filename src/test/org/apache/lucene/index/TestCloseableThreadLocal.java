begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|CloseableThreadLocal
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
name|util
operator|.
name|LuceneTestCase
import|;
end_import
begin_class
DECL|class|TestCloseableThreadLocal
specifier|public
class|class
name|TestCloseableThreadLocal
extends|extends
name|LuceneTestCase
block|{
DECL|field|TEST_VALUE
specifier|public
specifier|static
specifier|final
name|String
name|TEST_VALUE
init|=
literal|"initvaluetest"
decl_stmt|;
DECL|method|testInitValue
specifier|public
name|void
name|testInitValue
parameter_list|()
block|{
name|InitValueThreadLocal
name|tl
init|=
operator|new
name|InitValueThreadLocal
argument_list|()
decl_stmt|;
name|String
name|str
init|=
operator|(
name|String
operator|)
name|tl
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|TEST_VALUE
argument_list|,
name|str
argument_list|)
expr_stmt|;
block|}
DECL|class|InitValueThreadLocal
specifier|public
class|class
name|InitValueThreadLocal
extends|extends
name|CloseableThreadLocal
block|{
DECL|method|initialValue
specifier|protected
name|Object
name|initialValue
parameter_list|()
block|{
return|return
name|TEST_VALUE
return|;
block|}
block|}
block|}
end_class
end_unit
