begin_unit
begin_package
DECL|package|org.apache.lucene.swing.models
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|swing
operator|.
name|models
package|;
end_package
begin_comment
comment|/**  * Copyright 2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|math
operator|.
name|BigDecimal
import|;
end_import
begin_comment
comment|/**  * @author Jonathan Simon - jonathan_s_simon@yahoo.com  */
end_comment
begin_class
DECL|class|DataStore
specifier|public
class|class
name|DataStore
block|{
DECL|field|ITALIAN_CATEGORY
specifier|private
specifier|static
specifier|final
name|String
name|ITALIAN_CATEGORY
init|=
literal|"Italian"
decl_stmt|;
DECL|field|CUBAN_CATEGORY
specifier|private
specifier|static
specifier|final
name|String
name|CUBAN_CATEGORY
init|=
literal|"Cuban"
decl_stmt|;
DECL|field|STEAK_CATEGORY
specifier|private
specifier|static
specifier|final
name|String
name|STEAK_CATEGORY
init|=
literal|"Steak"
decl_stmt|;
DECL|field|id
specifier|private
specifier|static
name|int
name|id
init|=
literal|0
decl_stmt|;
DECL|field|restaurants
specifier|static
name|Collection
name|restaurants
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
DECL|field|pinos
specifier|static
name|RestaurantInfo
name|pinos
init|=
operator|new
name|RestaurantInfo
argument_list|()
decl_stmt|;
DECL|field|canolis
specifier|static
name|RestaurantInfo
name|canolis
init|=
operator|new
name|RestaurantInfo
argument_list|()
decl_stmt|;
DECL|field|picadillo
specifier|static
name|RestaurantInfo
name|picadillo
init|=
operator|new
name|RestaurantInfo
argument_list|()
decl_stmt|;
DECL|field|versailles
specifier|static
name|RestaurantInfo
name|versailles
init|=
operator|new
name|RestaurantInfo
argument_list|()
decl_stmt|;
DECL|field|laCaretta
specifier|static
name|RestaurantInfo
name|laCaretta
init|=
operator|new
name|RestaurantInfo
argument_list|()
decl_stmt|;
DECL|field|laCaretta2
specifier|static
name|RestaurantInfo
name|laCaretta2
init|=
operator|new
name|RestaurantInfo
argument_list|()
decl_stmt|;
DECL|field|laCaretta3
specifier|static
name|RestaurantInfo
name|laCaretta3
init|=
operator|new
name|RestaurantInfo
argument_list|()
decl_stmt|;
DECL|field|ranchaLuna
specifier|static
name|RestaurantInfo
name|ranchaLuna
init|=
operator|new
name|RestaurantInfo
argument_list|()
decl_stmt|;
DECL|field|leMerais
specifier|static
name|RestaurantInfo
name|leMerais
init|=
operator|new
name|RestaurantInfo
argument_list|()
decl_stmt|;
DECL|field|chris
specifier|static
name|RestaurantInfo
name|chris
init|=
operator|new
name|RestaurantInfo
argument_list|()
decl_stmt|;
DECL|field|outback
specifier|static
name|RestaurantInfo
name|outback
init|=
operator|new
name|RestaurantInfo
argument_list|()
decl_stmt|;
DECL|field|outback2
specifier|static
name|RestaurantInfo
name|outback2
init|=
operator|new
name|RestaurantInfo
argument_list|()
decl_stmt|;
DECL|field|outback3
specifier|static
name|RestaurantInfo
name|outback3
init|=
operator|new
name|RestaurantInfo
argument_list|()
decl_stmt|;
DECL|field|outback4
specifier|static
name|RestaurantInfo
name|outback4
init|=
operator|new
name|RestaurantInfo
argument_list|()
decl_stmt|;
DECL|method|getRestaurants
specifier|public
specifier|static
name|Iterator
name|getRestaurants
parameter_list|()
block|{
return|return
name|restaurants
operator|.
name|iterator
argument_list|()
return|;
block|}
static|static
block|{
name|pinos
operator|.
name|setId
argument_list|(
name|getNextId
argument_list|()
argument_list|)
expr_stmt|;
name|pinos
operator|.
name|setType
argument_list|(
name|ITALIAN_CATEGORY
argument_list|)
expr_stmt|;
name|pinos
operator|.
name|setName
argument_list|(
literal|"Pino's"
argument_list|)
expr_stmt|;
name|pinos
operator|.
name|setPhone
argument_list|(
literal|"(305) 111-2222"
argument_list|)
expr_stmt|;
name|pinos
operator|.
name|setStreet
argument_list|(
literal|"12115 105th Street "
argument_list|)
expr_stmt|;
name|pinos
operator|.
name|setCity
argument_list|(
literal|"Miami"
argument_list|)
expr_stmt|;
name|pinos
operator|.
name|setState
argument_list|(
literal|"FL"
argument_list|)
expr_stmt|;
name|pinos
operator|.
name|setZip
argument_list|(
literal|"33176"
argument_list|)
expr_stmt|;
name|restaurants
operator|.
name|add
argument_list|(
name|pinos
argument_list|)
expr_stmt|;
name|canolis
operator|.
name|setId
argument_list|(
name|getNextId
argument_list|()
argument_list|)
expr_stmt|;
name|canolis
operator|.
name|setType
argument_list|(
name|ITALIAN_CATEGORY
argument_list|)
expr_stmt|;
name|canolis
operator|.
name|setName
argument_list|(
literal|"Canoli's"
argument_list|)
expr_stmt|;
name|canolis
operator|.
name|setPhone
argument_list|(
literal|"(305) 234-5543"
argument_list|)
expr_stmt|;
name|canolis
operator|.
name|setStreet
argument_list|(
literal|"12123 85th Street "
argument_list|)
expr_stmt|;
name|canolis
operator|.
name|setCity
argument_list|(
literal|"Miami"
argument_list|)
expr_stmt|;
name|canolis
operator|.
name|setState
argument_list|(
literal|"FL"
argument_list|)
expr_stmt|;
name|canolis
operator|.
name|setZip
argument_list|(
literal|"33176"
argument_list|)
expr_stmt|;
name|restaurants
operator|.
name|add
argument_list|(
name|canolis
argument_list|)
expr_stmt|;
name|picadillo
operator|.
name|setId
argument_list|(
name|getNextId
argument_list|()
argument_list|)
expr_stmt|;
name|picadillo
operator|.
name|setType
argument_list|(
name|CUBAN_CATEGORY
argument_list|)
expr_stmt|;
name|picadillo
operator|.
name|setName
argument_list|(
literal|"Picadillo"
argument_list|)
expr_stmt|;
name|picadillo
operator|.
name|setPhone
argument_list|(
literal|"(305) 746-7865"
argument_list|)
expr_stmt|;
name|picadillo
operator|.
name|setStreet
argument_list|(
literal|"109 12th Street "
argument_list|)
expr_stmt|;
name|picadillo
operator|.
name|setCity
argument_list|(
literal|"Miami"
argument_list|)
expr_stmt|;
name|picadillo
operator|.
name|setState
argument_list|(
literal|"FL"
argument_list|)
expr_stmt|;
name|picadillo
operator|.
name|setZip
argument_list|(
literal|"33176"
argument_list|)
expr_stmt|;
name|restaurants
operator|.
name|add
argument_list|(
name|picadillo
argument_list|)
expr_stmt|;
name|versailles
operator|.
name|setId
argument_list|(
name|getNextId
argument_list|()
argument_list|)
expr_stmt|;
name|versailles
operator|.
name|setType
argument_list|(
name|CUBAN_CATEGORY
argument_list|)
expr_stmt|;
name|versailles
operator|.
name|setName
argument_list|(
literal|"Cafe Versailles"
argument_list|)
expr_stmt|;
name|versailles
operator|.
name|setPhone
argument_list|(
literal|"(305) 201-5438"
argument_list|)
expr_stmt|;
name|versailles
operator|.
name|setStreet
argument_list|(
literal|"312 8th Street "
argument_list|)
expr_stmt|;
name|versailles
operator|.
name|setCity
argument_list|(
literal|"Miami"
argument_list|)
expr_stmt|;
name|versailles
operator|.
name|setState
argument_list|(
literal|"FL"
argument_list|)
expr_stmt|;
name|versailles
operator|.
name|setZip
argument_list|(
literal|"33176"
argument_list|)
expr_stmt|;
name|restaurants
operator|.
name|add
argument_list|(
name|versailles
argument_list|)
expr_stmt|;
name|laCaretta
operator|.
name|setId
argument_list|(
name|getNextId
argument_list|()
argument_list|)
expr_stmt|;
name|laCaretta
operator|.
name|setType
argument_list|(
name|CUBAN_CATEGORY
argument_list|)
expr_stmt|;
name|laCaretta
operator|.
name|setName
argument_list|(
literal|"La Carretta"
argument_list|)
expr_stmt|;
name|laCaretta
operator|.
name|setPhone
argument_list|(
literal|"(305) 342-9876"
argument_list|)
expr_stmt|;
name|laCaretta
operator|.
name|setStreet
argument_list|(
literal|"348 8th Street "
argument_list|)
expr_stmt|;
name|laCaretta
operator|.
name|setCity
argument_list|(
literal|"Miami"
argument_list|)
expr_stmt|;
name|laCaretta
operator|.
name|setState
argument_list|(
literal|"FL"
argument_list|)
expr_stmt|;
name|laCaretta
operator|.
name|setZip
argument_list|(
literal|"33176"
argument_list|)
expr_stmt|;
name|restaurants
operator|.
name|add
argument_list|(
name|laCaretta
argument_list|)
expr_stmt|;
name|laCaretta2
operator|.
name|setId
argument_list|(
name|getNextId
argument_list|()
argument_list|)
expr_stmt|;
name|laCaretta2
operator|.
name|setType
argument_list|(
name|CUBAN_CATEGORY
argument_list|)
expr_stmt|;
name|laCaretta2
operator|.
name|setName
argument_list|(
literal|"La Carretta"
argument_list|)
expr_stmt|;
name|laCaretta2
operator|.
name|setPhone
argument_list|(
literal|"(305) 556-9876"
argument_list|)
expr_stmt|;
name|laCaretta2
operator|.
name|setStreet
argument_list|(
literal|"31224 23rd Street "
argument_list|)
expr_stmt|;
name|laCaretta2
operator|.
name|setCity
argument_list|(
literal|"Miami"
argument_list|)
expr_stmt|;
name|laCaretta2
operator|.
name|setState
argument_list|(
literal|"FL"
argument_list|)
expr_stmt|;
name|laCaretta2
operator|.
name|setZip
argument_list|(
literal|"33176"
argument_list|)
expr_stmt|;
name|restaurants
operator|.
name|add
argument_list|(
name|laCaretta2
argument_list|)
expr_stmt|;
name|laCaretta3
operator|.
name|setId
argument_list|(
name|getNextId
argument_list|()
argument_list|)
expr_stmt|;
name|laCaretta3
operator|.
name|setType
argument_list|(
name|CUBAN_CATEGORY
argument_list|)
expr_stmt|;
name|laCaretta3
operator|.
name|setName
argument_list|(
literal|"La Carretta"
argument_list|)
expr_stmt|;
name|laCaretta3
operator|.
name|setPhone
argument_list|(
literal|"(305) 682-9876"
argument_list|)
expr_stmt|;
name|laCaretta3
operator|.
name|setStreet
argument_list|(
literal|"23543 107th Street "
argument_list|)
expr_stmt|;
name|laCaretta3
operator|.
name|setCity
argument_list|(
literal|"Miami"
argument_list|)
expr_stmt|;
name|laCaretta3
operator|.
name|setState
argument_list|(
literal|"FL"
argument_list|)
expr_stmt|;
name|laCaretta3
operator|.
name|setZip
argument_list|(
literal|"33176"
argument_list|)
expr_stmt|;
name|restaurants
operator|.
name|add
argument_list|(
name|laCaretta3
argument_list|)
expr_stmt|;
name|ranchaLuna
operator|.
name|setId
argument_list|(
name|getNextId
argument_list|()
argument_list|)
expr_stmt|;
name|ranchaLuna
operator|.
name|setType
argument_list|(
name|CUBAN_CATEGORY
argument_list|)
expr_stmt|;
name|ranchaLuna
operator|.
name|setName
argument_list|(
literal|"Rancha Luna"
argument_list|)
expr_stmt|;
name|ranchaLuna
operator|.
name|setPhone
argument_list|(
literal|"(305) 777-4384"
argument_list|)
expr_stmt|;
name|ranchaLuna
operator|.
name|setStreet
argument_list|(
literal|"110 23rd Street "
argument_list|)
expr_stmt|;
name|ranchaLuna
operator|.
name|setCity
argument_list|(
literal|"Miami"
argument_list|)
expr_stmt|;
name|ranchaLuna
operator|.
name|setState
argument_list|(
literal|"FL"
argument_list|)
expr_stmt|;
name|ranchaLuna
operator|.
name|setZip
argument_list|(
literal|"33176"
argument_list|)
expr_stmt|;
name|restaurants
operator|.
name|add
argument_list|(
name|ranchaLuna
argument_list|)
expr_stmt|;
name|leMerais
operator|.
name|setId
argument_list|(
name|getNextId
argument_list|()
argument_list|)
expr_stmt|;
name|leMerais
operator|.
name|setType
argument_list|(
name|STEAK_CATEGORY
argument_list|)
expr_stmt|;
name|leMerais
operator|.
name|setName
argument_list|(
literal|"Le Merais"
argument_list|)
expr_stmt|;
name|leMerais
operator|.
name|setPhone
argument_list|(
literal|"(212) 654-9187"
argument_list|)
expr_stmt|;
name|leMerais
operator|.
name|setStreet
argument_list|(
literal|"11 West 46th Street"
argument_list|)
expr_stmt|;
name|leMerais
operator|.
name|setCity
argument_list|(
literal|"New York"
argument_list|)
expr_stmt|;
name|leMerais
operator|.
name|setState
argument_list|(
literal|"NY"
argument_list|)
expr_stmt|;
name|leMerais
operator|.
name|setZip
argument_list|(
literal|"10018"
argument_list|)
expr_stmt|;
name|restaurants
operator|.
name|add
argument_list|(
name|leMerais
argument_list|)
expr_stmt|;
name|chris
operator|.
name|setId
argument_list|(
name|getNextId
argument_list|()
argument_list|)
expr_stmt|;
name|chris
operator|.
name|setType
argument_list|(
name|STEAK_CATEGORY
argument_list|)
expr_stmt|;
name|chris
operator|.
name|setName
argument_list|(
literal|"Ruth's Chris Seakhouse"
argument_list|)
expr_stmt|;
name|chris
operator|.
name|setPhone
argument_list|(
literal|"(305) 354-8885"
argument_list|)
expr_stmt|;
name|chris
operator|.
name|setStreet
argument_list|(
literal|"12365 203rd Street "
argument_list|)
expr_stmt|;
name|chris
operator|.
name|setCity
argument_list|(
literal|"Miami"
argument_list|)
expr_stmt|;
name|chris
operator|.
name|setState
argument_list|(
literal|"FL"
argument_list|)
expr_stmt|;
name|chris
operator|.
name|setZip
argument_list|(
literal|"33176"
argument_list|)
expr_stmt|;
name|restaurants
operator|.
name|add
argument_list|(
name|chris
argument_list|)
expr_stmt|;
name|outback
operator|.
name|setId
argument_list|(
name|getNextId
argument_list|()
argument_list|)
expr_stmt|;
name|outback
operator|.
name|setType
argument_list|(
name|STEAK_CATEGORY
argument_list|)
expr_stmt|;
name|outback
operator|.
name|setName
argument_list|(
literal|"Outback"
argument_list|)
expr_stmt|;
name|outback
operator|.
name|setPhone
argument_list|(
literal|"(305) 244-7623"
argument_list|)
expr_stmt|;
name|outback
operator|.
name|setStreet
argument_list|(
literal|"348 136th Street "
argument_list|)
expr_stmt|;
name|outback
operator|.
name|setCity
argument_list|(
literal|"Miami"
argument_list|)
expr_stmt|;
name|outback
operator|.
name|setState
argument_list|(
literal|"FL"
argument_list|)
expr_stmt|;
name|outback
operator|.
name|setZip
argument_list|(
literal|"33176"
argument_list|)
expr_stmt|;
name|restaurants
operator|.
name|add
argument_list|(
name|outback
argument_list|)
expr_stmt|;
name|outback2
operator|.
name|setId
argument_list|(
name|getNextId
argument_list|()
argument_list|)
expr_stmt|;
name|outback2
operator|.
name|setType
argument_list|(
name|STEAK_CATEGORY
argument_list|)
expr_stmt|;
name|outback2
operator|.
name|setName
argument_list|(
literal|"Outback"
argument_list|)
expr_stmt|;
name|outback2
operator|.
name|setPhone
argument_list|(
literal|"(305) 533-6522"
argument_list|)
expr_stmt|;
name|outback2
operator|.
name|setStreet
argument_list|(
literal|"21 207th Street "
argument_list|)
expr_stmt|;
name|outback2
operator|.
name|setCity
argument_list|(
literal|"Miami"
argument_list|)
expr_stmt|;
name|outback2
operator|.
name|setState
argument_list|(
literal|"FL"
argument_list|)
expr_stmt|;
name|outback2
operator|.
name|setZip
argument_list|(
literal|"33176"
argument_list|)
expr_stmt|;
name|restaurants
operator|.
name|add
argument_list|(
name|outback2
argument_list|)
expr_stmt|;
name|outback3
operator|.
name|setId
argument_list|(
name|getNextId
argument_list|()
argument_list|)
expr_stmt|;
name|outback3
operator|.
name|setType
argument_list|(
name|STEAK_CATEGORY
argument_list|)
expr_stmt|;
name|outback3
operator|.
name|setName
argument_list|(
literal|"Outback"
argument_list|)
expr_stmt|;
name|outback3
operator|.
name|setPhone
argument_list|(
literal|"(305) 244-7623"
argument_list|)
expr_stmt|;
name|outback3
operator|.
name|setStreet
argument_list|(
literal|"10117 107th Street "
argument_list|)
expr_stmt|;
name|outback3
operator|.
name|setCity
argument_list|(
literal|"Miami"
argument_list|)
expr_stmt|;
name|outback3
operator|.
name|setState
argument_list|(
literal|"FL"
argument_list|)
expr_stmt|;
name|outback3
operator|.
name|setZip
argument_list|(
literal|"33176"
argument_list|)
expr_stmt|;
name|restaurants
operator|.
name|add
argument_list|(
name|outback3
argument_list|)
expr_stmt|;
name|outback4
operator|.
name|setId
argument_list|(
name|getNextId
argument_list|()
argument_list|)
expr_stmt|;
name|outback4
operator|.
name|setType
argument_list|(
name|STEAK_CATEGORY
argument_list|)
expr_stmt|;
name|outback4
operator|.
name|setName
argument_list|(
literal|"Outback"
argument_list|)
expr_stmt|;
name|outback4
operator|.
name|setPhone
argument_list|(
literal|"(954) 221-3312"
argument_list|)
expr_stmt|;
name|outback4
operator|.
name|setStreet
argument_list|(
literal|"10 11th Street "
argument_list|)
expr_stmt|;
name|outback4
operator|.
name|setCity
argument_list|(
literal|"Aventura"
argument_list|)
expr_stmt|;
name|outback4
operator|.
name|setState
argument_list|(
literal|"FL"
argument_list|)
expr_stmt|;
name|outback4
operator|.
name|setZip
argument_list|(
literal|"32154"
argument_list|)
expr_stmt|;
name|restaurants
operator|.
name|add
argument_list|(
name|outback4
argument_list|)
expr_stmt|;
block|}
DECL|method|getNextId
specifier|private
specifier|static
name|int
name|getNextId
parameter_list|()
block|{
name|id
operator|++
expr_stmt|;
return|return
name|id
return|;
block|}
block|}
end_class
end_unit
