begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.hunspell
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|hunspell
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipEntry
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipFile
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
name|hunspell
operator|.
name|Dictionary
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
name|RamUsageTester
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import
begin_comment
comment|/**  * These thunderbird dictionaries can be retrieved via:  * https://addons.mozilla.org/en-US/thunderbird/language-tools/  * You must click and download every file: sorry!  */
end_comment
begin_class
annotation|@
name|Ignore
argument_list|(
literal|"enable manually"
argument_list|)
DECL|class|TestAllDictionaries2
specifier|public
class|class
name|TestAllDictionaries2
extends|extends
name|LuceneTestCase
block|{
comment|// set this to the location of where you downloaded all the files
DECL|field|DICTIONARY_HOME
specifier|static
specifier|final
name|File
name|DICTIONARY_HOME
init|=
operator|new
name|File
argument_list|(
literal|"/data/thunderbirdDicts"
argument_list|)
decl_stmt|;
DECL|field|tests
specifier|final
name|String
name|tests
index|[]
init|=
block|{
comment|/* zip file */
comment|/* dictionary */
comment|/* affix */
literal|"addon-0.4.5-an+fx+tb+fn+sm.xpi"
block|,
literal|"dictionaries/ru.dic"
block|,
literal|"dictionaries/ru.aff"
block|,
literal|"addon-0.5.5-fx+tb.xpi"
block|,
literal|"dictionaries/ko-KR.dic"
block|,
literal|"dictionaries/ko-KR.aff"
block|,
literal|"afrikaans_spell_checker-20110323-fx+tb+fn+sm.xpi"
block|,
literal|"dictionaries/af-ZA.dic"
block|,
literal|"dictionaries/af-ZA.aff"
block|,
literal|"albanisches_worterbuch-1.6.9-fx+tb+sm+fn.xpi"
block|,
literal|"dictionaries/sq.dic"
block|,
literal|"dictionaries/sq.aff"
block|,
literal|"amharic_spell_checker-0.4-fx+fn+tb+sm.xpi"
block|,
literal|"dictionaries/am_ET.dic"
block|,
literal|"dictionaries/am_ET.aff"
block|,
literal|"arabic_spell_checking_dictionary-3.2.20120321-fx+tb.xpi"
block|,
literal|"dictionaries/ar.dic"
block|,
literal|"dictionaries/ar.aff"
block|,
literal|"armenian_spell_checker_dictionary-0.32-fx+tb+sm.xpi"
block|,
literal|"dictionaries/hy_AM.dic"
block|,
literal|"dictionaries/hy_AM.aff"
block|,
literal|"azerbaijani_spell_checker-0.3-fx+tb+fn+sm+sb.xpi"
block|,
literal|"dictionaries/az-Latn-AZ.dic"
block|,
literal|"dictionaries/az-Latn-AZ.aff"
block|,
literal|"belarusian_classic_dictionary-0.1.2-tb+fx+sm.xpi"
block|,
literal|"dictionaries/be-classic.dic"
block|,
literal|"dictionaries/be-classic.aff"
block|,
literal|"belarusian_dictionary-0.1.2-fx+sm+tb.xpi"
block|,
literal|"dictionaries/be.dic"
block|,
literal|"dictionaries/be.aff"
block|,
literal|"bengali_bangladesh_dictionary-0.08-sm+tb+fx.xpi"
block|,
literal|"dictionaries/bn-BD.dic"
block|,
literal|"dictionaries/bn-BD.aff"
block|,
literal|"brazilian_portuguese_dictionary_former_spelling-28.20140203-tb+sm+fx.xpi"
block|,
literal|"dictionaries/pt-BR-antigo.dic"
block|,
literal|"dictionaries/pt-BR-antigo.aff"
block|,
literal|"brazilian_portuguese_dictionary_new_spelling-28.20140203-fx+sm+tb.xpi"
block|,
literal|"dictionaries/pt-BR.dic"
block|,
literal|"dictionaries/pt-BR.aff"
block|,
literal|"british_english_dictionary_updated-1.19.5-sm+fx+tb.xpi"
block|,
literal|"dictionaries/en-GB.dic"
block|,
literal|"dictionaries/en-GB.aff"
block|,
literal|"bulgarian_dictionary-4.3-fx+tb+sm.xpi"
block|,
literal|"dictionaries/bg.dic"
block|,
literal|"dictionaries/bg.aff"
block|,
literal|"canadian_english_dictionary-2.0.8-fx+sm+tb.xpi"
block|,
literal|"dictionaries/en-CA.dic"
block|,
literal|"dictionaries/en-CA.aff"
block|,
literal|"ceske_slovniky_pro_kontrolu_pravopisu-1.0.4-tb+sm+fx.xpi"
block|,
literal|"dictionaries/cs.dic"
block|,
literal|"dictionaries/cs.aff"
block|,
literal|"chichewa_spell_checker-0.3-fx+tb+fn+sm+sb.xpi"
block|,
literal|"dictionaries/ny_MW.dic"
block|,
literal|"dictionaries/ny_MW.aff"
block|,
literal|"corrector_de_galego-13.10.0-fn+sm+tb+fx.xpi"
block|,
literal|"dictionaries/gl_ES.dic"
block|,
literal|"dictionaries/gl_ES.aff"
block|,
comment|//BUG: broken flags "corrector_orthographic_de_interlingua-6.0-fn+sm+tb+fx.xpi",                      "dictionaries/ia-ia.dic",             "dictionaries/ia-ia.aff",
literal|"corrector_ortografico_aragones-0.2-fx+tb+sm.xpi"
block|,
literal|"dictionaries/an_ES.dic"
block|,
literal|"dictionaries/an_ES.aff"
block|,
literal|"croatian_dictionary_-_hrvatski_rjecnik-1.0.1-firefox+thunderbird+seamonkey.xpi"
block|,
literal|"dictionaries/hr.dic"
block|,
literal|"dictionaries/hr.aff"
block|,
literal|"croatian_dictionary_hrvatski_rjecnik-1.0.9-an+fx+fn+tb+sm.xpi"
block|,
literal|"dictionaries/hr-HR.dic"
block|,
literal|"dictionaries/hr-HR.aff"
block|,
literal|"dansk_ordbog_til_stavekontrollen-2.2.1-sm+tb+fx.xpi"
block|,
literal|"dictionaries/da.dic"
block|,
literal|"dictionaries/da.aff"
block|,
literal|"deutsches_worterbuch_de_de_alte_rechtschreibung-2.1.8-sm.xpi"
block|,
literal|"dictionaries/de-DE-1901.dic"
block|,
literal|"dictionaries/de-DE-1901.aff"
block|,
literal|"diccionario_de_espanolespana-1.7-sm+tb+fn+fx.xpi"
block|,
literal|"dictionaries/es-ES.dic"
block|,
literal|"dictionaries/es-ES.aff"
block|,
literal|"diccionario_en_espanol_para_venezuela-1.1.17-sm+an+tb+fn+fx.xpi"
block|,
literal|"dictionaries/es_VE.dic"
block|,
literal|"dictionaries/es_VE.aff"
block|,
literal|"diccionario_espanol_argentina-2.5.1-tb+fx+sm.xpi"
block|,
literal|"dictionaries/es_AR.dic"
block|,
literal|"dictionaries/es_AR.aff"
block|,
literal|"diccionario_espanol_mexico-1.1.3-fn+tb+fx+sm.xpi"
block|,
literal|"dictionaries/es_MX.dic"
block|,
literal|"dictionaries/es_MX.aff"
block|,
literal|"diccionario_ortografico_valenciano-2.2.0-fx+tb+fn+sm.xpi"
block|,
literal|"dictionaries/roa-ES-val.dic"
block|,
literal|"dictionaries/roa-ES-val.aff"
block|,
literal|"diccionario_papiamentoaruba-0.2-fn+sm+tb+fx.xpi"
block|,
literal|"dictionaries/Papiamento.dic"
block|,
literal|"dictionaries/Papiamento.aff"
block|,
literal|"dictionnaires_francais-5.0.2-fx+tb+sm.xpi"
block|,
literal|"dictionaries/fr-classic-reform.dic"
block|,
literal|"dictionaries/fr-classic-reform.aff"
block|,
literal|"dictionnaires_francais-5.0.2-fx+tb+sm.xpi"
block|,
literal|"dictionaries/fr-classic.dic"
block|,
literal|"dictionaries/fr-classic.aff"
block|,
literal|"dictionnaires_francais-5.0.2-fx+tb+sm.xpi"
block|,
literal|"dictionaries/fr-modern.dic"
block|,
literal|"dictionaries/fr-modern.aff"
block|,
literal|"dictionnaires_francais-5.0.2-fx+tb+sm.xpi"
block|,
literal|"dictionaries/fr-reform.dic"
block|,
literal|"dictionaries/fr-reform.aff"
block|,
literal|"difazier_an_drouizig-0.12-tb+sm+fx.xpi"
block|,
literal|"dictionaries/br.dic"
block|,
literal|"dictionaries/br.aff"
block|,
literal|"dikshonario_papiamentuantia_hulandes-0.5-fx+tb+fn+sb+sm.xpi"
block|,
literal|"dictionaries/Papiamentu.dic"
block|,
literal|"dictionaries/Papiamentu.aff"
block|,
literal|"dizionari_furlan-3.1-tb+fx+sm.xpi"
block|,
literal|"dictionaries/fur-IT.dic"
block|,
literal|"dictionaries/fur-IT.aff"
block|,
literal|"dizionario_italiano-3.3.2-fx+sm+tb.xpi"
block|,
literal|"dictionaries/it_IT.dic"
block|,
literal|"dictionaries/it_IT.aff"
block|,
literal|"eesti_keele_speller-3.2-fx+tb+sm.xpi"
block|,
literal|"dictionaries/et-EE.dic"
block|,
literal|"dictionaries/et-EE.aff"
block|,
literal|"english_australian_dictionary-2.1.2-tb+fx+sm.xpi"
block|,
literal|"dictionaries/en-AU.dic"
block|,
literal|"dictionaries/en-AU.aff"
block|,
literal|"esperanta_vortaro-1.0.2-fx+tb+sm.xpi"
block|,
literal|"dictionaries/eo-EO.dic"
block|,
literal|"dictionaries/eo-EO.aff"
block|,
literal|"european_portuguese_spellchecker-14.1.1.1-tb+fx.xpi"
block|,
literal|"dictionaries/pt-PT.dic"
block|,
literal|"dictionaries/pt-PT.aff"
block|,
literal|"faroese_spell_checker_faroe_islands-2.0-tb+sm+fx+fn.xpi"
block|,
literal|"dictionaries/fo_FO.dic"
block|,
literal|"dictionaries/fo_FO.aff"
block|,
literal|"frysk_wurdboek-2.1.1-fn+sm+fx+an+tb.xpi"
block|,
literal|"dictionaries/fy.dic"
block|,
literal|"dictionaries/fy.aff"
block|,
literal|"geiriadur_cymraeg-1.08-tb+sm+fx.xpi"
block|,
literal|"dictionaries/cy_GB.dic"
block|,
literal|"dictionaries/cy_GB.aff"
block|,
literal|"general_catalan_dictionary-2.5.0-tb+sm+fn+fx.xpi"
block|,
literal|"dictionaries/ca.dic"
block|,
literal|"dictionaries/ca.aff"
block|,
literal|"german_dictionary-2.0.3-fn+fx+sm+tb.xpi"
block|,
literal|"dictionaries/de-DE.dic"
block|,
literal|"dictionaries/de-DE.aff"
block|,
literal|"german_dictionary_de_at_new_orthography-20130905-tb+fn+an+fx+sm.xpi"
block|,
literal|"dictionaries/de-AT.dic"
block|,
literal|"dictionaries/de-AT.aff"
block|,
literal|"german_dictionary_de_ch_new_orthography-20130905-fx+tb+fn+sm+an.xpi"
block|,
literal|"dictionaries/de-CH.dic"
block|,
literal|"dictionaries/de-CH.aff"
block|,
literal|"german_dictionary_de_de_new_orthography-20130905-tb+sm+an+fn+fx.xpi"
block|,
literal|"dictionaries/de-DE.dic"
block|,
literal|"dictionaries/de-DE.aff"
block|,
literal|"german_dictionary_extended_for_austria-2.0.3-fx+fn+sm+tb.xpi"
block|,
literal|"dictionaries/de-AT.dic"
block|,
literal|"dictionaries/de-AT.aff"
block|,
literal|"german_dictionary_switzerland-2.0.3-sm+fx+tb+fn.xpi"
block|,
literal|"dictionaries/de-CH.dic"
block|,
literal|"dictionaries/de-CH.aff"
block|,
literal|"greek_spelling_dictionary-0.8.5-fx+tb+sm.xpi"
block|,
literal|"dictionaries/el-GR.dic"
block|,
literal|"dictionaries/el-GR.aff"
block|,
literal|"gujarati_spell_checker-0.3-fx+tb+fn+sm+sb.xpi"
block|,
literal|"dictionaries/gu_IN.dic"
block|,
literal|"dictionaries/gu_IN.aff"
block|,
literal|"haitian_creole_spell_checker-0.08-tb+sm+fx.xpi"
block|,
literal|"dictionaries/ht-HT.dic"
block|,
literal|"dictionaries/ht-HT.aff"
block|,
literal|"hausa_spelling_dictionary-0.2-tb+fx.xpi"
block|,
literal|"dictionaries/ha-GH.dic"
block|,
literal|"dictionaries/ha-GH.aff"
block|,
literal|"hebrew_spell_checking_dictionary_from_hspell-1.2.0.1-fx+sm+tb.xpi"
block|,
literal|"dictionaries/he.dic"
block|,
literal|"dictionaries/he.aff"
block|,
literal|"hindi_spell_checker-0.4-fx+tb+sm+sb+fn.xpi"
block|,
literal|"dictionaries/hi_IN.dic"
block|,
literal|"dictionaries/hi_IN.aff"
block|,
literal|"hungarian_dictionary-1.6.1.1-fx+tb+sm+fn.xpi"
block|,
literal|"dictionaries/hu.dic"
block|,
literal|"dictionaries/hu.aff"
block|,
comment|//BUG: has no encoding declaration "icelandic_dictionary-1.3-fx+tb+sm.xpi",                                          "dictionaries/is.dic",                "dictionaries/is.aff",
literal|"kamus_pengecek_ejaan_bahasa_indonesia-1.1-fx+tb.xpi"
block|,
literal|"dictionaries/id.dic"
block|,
literal|"dictionaries/id.aff"
block|,
literal|"kannada_spell_checker-2.0.1-tb+sm+fn+an+fx.xpi"
block|,
literal|"dictionaries/kn.dic"
block|,
literal|"dictionaries/kn.aff"
block|,
literal|"kashubian_spell_checker_poland-0.9-sm+tb+fx.xpi"
block|,
literal|"dictionaries/Kaszebsczi.dic"
block|,
literal|"dictionaries/Kaszebsczi.aff"
block|,
literal|"kiswahili_spell_checker-0.3-sb+tb+fn+fx+sm.xpi"
block|,
literal|"dictionaries/sw_TZ.dic"
block|,
literal|"dictionaries/sw_TZ.aff"
block|,
literal|"kurdish_spell_checker-0.96-fx+tb+sm.xpi"
block|,
literal|"dictionaries/ku-TR.dic"
block|,
literal|"dictionaries/ku-TR.aff"
block|,
literal|"lao_spellchecking_dictionary-0-fx+tb+sm+fn+an.xpi"
block|,
literal|"dictionaries/lo_LA.dic"
block|,
literal|"dictionaries/lo_LA.aff"
block|,
literal|"latviesu_valodas_pareizrakstibas_parbaudes_vardnica-1.0.0-fn+fx+tb+sm.xpi"
block|,
literal|"dictionaries/lv_LV.dic"
block|,
literal|"dictionaries/lv_LV.aff"
block|,
literal|"lithuanian_spelling_check_dictionary-1.3-fx+tb+sm+fn.xpi"
block|,
literal|"dictionaries/lt.dic"
block|,
literal|"dictionaries/lt.aff"
block|,
literal|"litreoir_gaelspell_do_mhozilla-4.7-tb+fx+sm+fn.xpi"
block|,
literal|"dictionaries/ga.dic"
block|,
literal|"dictionaries/ga.aff"
block|,
literal|"litreoir_na_liongailise-0.03-fx+sm+tb.xpi"
block|,
literal|"dictionaries/ln-CD.dic"
block|,
literal|"dictionaries/ln-CD.aff"
block|,
literal|"macedonian_mk_mk_spellchecker-1.2-fn+tb+fx+sm+sb.xpi"
block|,
literal|"dictionaries/mk-MK-Cyrl.dic"
block|,
literal|"dictionaries/mk-MK-Cyrl.aff"
block|,
literal|"macedonian_mk_mk_spellchecker-1.2-fn+tb+fx+sm+sb.xpi"
block|,
literal|"dictionaries/mk-MK-Latn.dic"
block|,
literal|"dictionaries/mk-MK-Latn.aff"
block|,
literal|"malagasy_spell_checker-0.3-fn+tb+fx+sm+sb.xpi"
block|,
literal|"dictionaries/mg_MG.dic"
block|,
literal|"dictionaries/mg_MG.aff"
block|,
literal|"marathi_dictionary-9.3-sm+tb+sb+fx.xpi"
block|,
literal|"dictionaries/mr-IN.dic"
block|,
literal|"dictionaries/mr-IN.aff"
block|,
literal|"ndebele_south_spell_checker-20110323-tb+fn+fx+sm.xpi"
block|,
literal|"dictionaries/nr-ZA.dic"
block|,
literal|"dictionaries/nr-ZA.aff"
block|,
literal|"nepali_dictionary-1.2-fx+tb.xpi"
block|,
literal|"dictionaries/ne_NP.dic"
block|,
literal|"dictionaries/ne_NP.aff"
block|,
literal|"norsk_bokmal_ordliste-2.0.10.2-fx+tb+sm.xpi"
block|,
literal|"dictionaries/nb.dic"
block|,
literal|"dictionaries/nb.aff"
block|,
literal|"norsk_nynorsk_ordliste-2.1.0-sm+fx+tb.xpi"
block|,
literal|"dictionaries/nn.dic"
block|,
literal|"dictionaries/nn.aff"
block|,
literal|"northern_sotho_spell_checker-20110323-tb+fn+fx+sm.xpi"
block|,
literal|"dictionaries/nso-ZA.dic"
block|,
literal|"dictionaries/nso-ZA.aff"
block|,
literal|"oriya_spell_checker-0.3-fn+tb+fx+sm+sb.xpi"
block|,
literal|"dictionaries/or-IN.dic"
block|,
literal|"dictionaries/or-IN.aff"
block|,
literal|"polski_slownik_poprawnej_pisowni-1.0.20110621-fx+tb+sm.xpi"
block|,
literal|"dictionaries/pl.dic"
block|,
literal|"dictionaries/pl.aff"
block|,
literal|"punjabi_spell_checker-0.3-fx+tb+sm+sb+fn.xpi"
block|,
literal|"dictionaries/pa-IN.dic"
block|,
literal|"dictionaries/pa-IN.aff"
block|,
literal|"romanian_spellchecking_dictionary-1.14-sm+tb+fx.xpi"
block|,
literal|"dictionaries/ro_RO-ante1993.dic"
block|,
literal|"dictionaries/ro_RO-ante1993.aff"
block|,
literal|"russian_hunspell_dictionary-1.0.20131101-tb+sm+fn+fx.xpi"
block|,
literal|"dictionaries/ru_RU.dic"
block|,
literal|"dictionaries/ru_RU.aff"
block|,
literal|"sanskrit_spell_checker-1.1-fx+tb+sm+sb+fn.xpi"
block|,
literal|"dictionaries/sa_IN.dic"
block|,
literal|"dictionaries/sa_IN.aff"
block|,
literal|"scottish_gaelic_spell_checker-2.7-tb+fx+sm.xpi"
block|,
literal|"dictionaries/gd-GB.dic"
block|,
literal|"dictionaries/gd-GB.aff"
block|,
literal|"serbian_dictionary-0.18-fx+tb+sm.xpi"
block|,
literal|"dictionaries/sr-RS-Cyrl.dic"
block|,
literal|"dictionaries/sr-RS-Cyrl.aff"
block|,
literal|"serbian_dictionary-0.18-fx+tb+sm.xpi"
block|,
literal|"dictionaries/sr-RS-Latn.dic"
block|,
literal|"dictionaries/sr-RS-Latn.aff"
block|,
literal|"slovak_spell_checking_dictionary-2.04.0-tb+fx+sm.xpi"
block|,
literal|"dictionaries/sk-SK.dic"
block|,
literal|"dictionaries/sk-SK.aff"
block|,
literal|"slovak_spell_checking_dictionary-2.04.0-tb+fx+sm.xpi"
block|,
literal|"dictionaries/sk-SK-ascii.dic"
block|,
literal|"dictionaries/sk-SK-ascii.aff"
block|,
literal|"slovar_za_slovenski_jezik-0.1.1.1-fx+tb+sm.xpi"
block|,
literal|"dictionaries/sl.dic"
block|,
literal|"dictionaries/sl.aff"
block|,
literal|"songhay_spell_checker-0.03-fx+tb+sm.xpi"
block|,
literal|"dictionaries/Songhay - Mali.dic"
block|,
literal|"dictionaries/Songhay - Mali.aff"
block|,
literal|"southern_sotho_spell_checker-20110323-tb+fn+fx+sm.xpi"
block|,
literal|"dictionaries/st-ZA.dic"
block|,
literal|"dictionaries/st-ZA.aff"
block|,
literal|"sownik_acinski-0.41.20110603-tb+fx+sm.xpi"
block|,
literal|"dictionaries/la.dic"
block|,
literal|"dictionaries/la.aff"
block|,
literal|"sownik_jezyka_dolnouzyckiego-1.4.8-an+fx+tb+fn+sm.xpi"
block|,
literal|"dictionaries/dsb.dic"
block|,
literal|"dictionaries/dsb.aff"
block|,
literal|"srpska_latinica-0.1-fx+tb+sm.xpi"
block|,
literal|"dictionaries/Srpski_latinica.dic"
block|,
literal|"dictionaries/Srpski_latinica.aff"
block|,
literal|"svenska_fria_ordlistan-1.1-tb+sm+fx.xpi"
block|,
literal|"dictionaries/sv.dic"
block|,
literal|"dictionaries/sv.aff"
block|,
literal|"svenska_fria_ordlistan-1.1-tb+sm+fx.xpi"
block|,
literal|"dictionaries/sv_FI.dic"
block|,
literal|"dictionaries/sv_FI.aff"
block|,
literal|"swati_spell_checker-20110323-tb+sm+fx+fn.xpi"
block|,
literal|"dictionaries/ss-ZA.dic"
block|,
literal|"dictionaries/ss-ZA.aff"
block|,
literal|"tamil_spell_checker_for_firefox-0.4-tb+fx.xpi"
block|,
literal|"dictionaries/ta-TA.dic"
block|,
literal|"dictionaries/ta-TA.aff"
block|,
literal|"telugu_spell_checker-0.3-tb+fx+sm.xpi"
block|,
literal|"dictionaries/te_IN.dic"
block|,
literal|"dictionaries/te_IN.aff"
block|,
literal|"te_papakupu_m__ori-0.9.9.20080630-fx+tb.xpi"
block|,
literal|"dictionaries/mi-x-Tai Tokerau.dic"
block|,
literal|"dictionaries/mi-x-Tai Tokerau.aff"
block|,
literal|"te_papakupu_m__ori-0.9.9.20080630-fx+tb.xpi"
block|,
literal|"dictionaries/mi.dic"
block|,
literal|"dictionaries/mi.aff"
block|,
comment|//BUG: broken file (hunspell refuses to load, too)    "thamizha_solthiruthitamil_spellchecker-0.8-fx+tb.xpi",                           "dictionaries/ta_IN.dic",             "dictionaries/ta_IN.aff",
literal|"tsonga_spell_checker-20110323-tb+sm+fx+fn.xpi"
block|,
literal|"dictionaries/ts-ZA.dic"
block|,
literal|"dictionaries/ts-ZA.aff"
block|,
literal|"tswana_spell_checker-20110323-tb+sm+fx+fn.xpi"
block|,
literal|"dictionaries/tn-ZA.dic"
block|,
literal|"dictionaries/tn-ZA.aff"
block|,
comment|//BUG: missing FLAG declaration "turkce_yazm_denetimi-3.5-sm+tb+fx.xpi",                                          "dictionaries/tr.dic",                "dictionaries/tr.aff",
literal|"turkmen_spell_checker_dictionary-0.1.6-tb+fx+sm.xpi"
block|,
literal|"dictionaries/tk_TM.dic"
block|,
literal|"dictionaries/tk_TM.aff"
block|,
literal|"ukrainian_dictionary-1.7.0-sm+an+fx+fn+tb.xpi"
block|,
literal|"dictionaries/uk-UA.dic"
block|,
literal|"dictionaries/uk-UA.aff"
block|,
literal|"united_states_english_spellchecker-7.0.1-sm+tb+fx+an.xpi"
block|,
literal|"dictionaries/en-US.dic"
block|,
literal|"dictionaries/en-US.aff"
block|,
literal|"upper_sorbian_spelling_dictionary-0.0.20060327.3-tb+fx+sm.xpi"
block|,
literal|"dictionaries/hsb.dic"
block|,
literal|"dictionaries/hsb.aff"
block|,
literal|"urdu_dictionary-0.64-fx+tb+sm+sb.xpi"
block|,
literal|"dictionaries/ur.dic"
block|,
literal|"dictionaries/ur.aff"
block|,
literal|"uzbek_spell_checker-0.3-fn+tb+fx+sm+sb.xpi"
block|,
literal|"dictionaries/uz.dic"
block|,
literal|"dictionaries/uz.aff"
block|,
literal|"valencian_catalan_dictionary-2.5.0-tb+fn+sm+fx.xpi"
block|,
literal|"dictionaries/ca-ES-valencia.dic"
block|,
literal|"dictionaries/ca-ES-valencia.aff"
block|,
literal|"venda_spell_checker-20110323-tb+fn+fx+sm.xpi"
block|,
literal|"dictionaries/ve-ZA.dic"
block|,
literal|"dictionaries/ve-ZA.aff"
block|,
literal|"verificador_ortografico_para_portugues_do_brasil-2.3-3.2b1-tb+sm+fn+fx.xpi"
block|,
literal|"dictionaries/pt_BR.dic"
block|,
literal|"dictionaries/pt_BR.aff"
block|,
literal|"vietnamese_dictionary-2.1.0.159-an+sm+tb+fx+fn.xpi"
block|,
literal|"dictionaries/vi-DauCu.dic"
block|,
literal|"dictionaries/vi-DauCu.aff"
block|,
literal|"vietnamese_dictionary-2.1.0.159-an+sm+tb+fx+fn.xpi"
block|,
literal|"dictionaries/vi-DauMoi.dic"
block|,
literal|"dictionaries/vi-DauMoi.aff"
block|,
literal|"woordenboek_nederlands-3.1.1-sm+tb+fx+fn.xpi"
block|,
literal|"dictionaries/nl.dic"
block|,
literal|"dictionaries/nl.aff"
block|,
literal|"xhosa_spell_checker-20110323-tb+fn+fx+sm.xpi"
block|,
literal|"dictionaries/xh-ZA.dic"
block|,
literal|"dictionaries/xh-ZA.aff"
block|,
literal|"xuxen-4.0.1-fx+tb+sm.xpi"
block|,
literal|"dictionaries/eu.dic"
block|,
literal|"dictionaries/eu.aff"
block|,
literal|"yiddish_spell_checker_yivo-0.0.3-sm+fn+fx+tb.xpi"
block|,
literal|"dictionaries/yi.dic"
block|,
literal|"dictionaries/yi.aff"
block|,
literal|"zulu_spell_checker-20110323-tb+fn+fx+sm.xpi"
block|,
literal|"dictionaries/zu-ZA.dic"
block|,
literal|"dictionaries/zu-ZA.aff"
block|}
decl_stmt|;
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|tests
operator|.
name|length
condition|;
name|i
operator|+=
literal|3
control|)
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|DICTIONARY_HOME
argument_list|,
name|tests
index|[
name|i
index|]
argument_list|)
decl_stmt|;
assert|assert
name|f
operator|.
name|exists
argument_list|()
assert|;
try|try
init|(
name|ZipFile
name|zip
init|=
operator|new
name|ZipFile
argument_list|(
name|f
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
init|)
block|{
name|ZipEntry
name|dicEntry
init|=
name|zip
operator|.
name|getEntry
argument_list|(
name|tests
index|[
name|i
operator|+
literal|1
index|]
argument_list|)
decl_stmt|;
assert|assert
name|dicEntry
operator|!=
literal|null
assert|;
name|ZipEntry
name|affEntry
init|=
name|zip
operator|.
name|getEntry
argument_list|(
name|tests
index|[
name|i
operator|+
literal|2
index|]
argument_list|)
decl_stmt|;
assert|assert
name|affEntry
operator|!=
literal|null
assert|;
try|try
init|(
name|InputStream
name|dictionary
init|=
name|zip
operator|.
name|getInputStream
argument_list|(
name|dicEntry
argument_list|)
init|;
name|InputStream
name|affix
operator|=
name|zip
operator|.
name|getInputStream
argument_list|(
name|affEntry
argument_list|)
init|)
block|{
name|Dictionary
name|dic
init|=
operator|new
name|Dictionary
argument_list|(
name|affix
argument_list|,
name|dictionary
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|tests
index|[
name|i
index|]
operator|+
literal|"\t"
operator|+
name|RamUsageTester
operator|.
name|humanSizeOf
argument_list|(
name|dic
argument_list|)
operator|+
literal|"\t("
operator|+
literal|"words="
operator|+
name|RamUsageTester
operator|.
name|humanSizeOf
argument_list|(
name|dic
operator|.
name|words
argument_list|)
operator|+
literal|", "
operator|+
literal|"flags="
operator|+
name|RamUsageTester
operator|.
name|humanSizeOf
argument_list|(
name|dic
operator|.
name|flagLookup
argument_list|)
operator|+
literal|", "
operator|+
literal|"strips="
operator|+
name|RamUsageTester
operator|.
name|humanSizeOf
argument_list|(
name|dic
operator|.
name|stripData
argument_list|)
operator|+
literal|", "
operator|+
literal|"conditions="
operator|+
name|RamUsageTester
operator|.
name|humanSizeOf
argument_list|(
name|dic
operator|.
name|patterns
argument_list|)
operator|+
literal|", "
operator|+
literal|"affixData="
operator|+
name|RamUsageTester
operator|.
name|humanSizeOf
argument_list|(
name|dic
operator|.
name|affixData
argument_list|)
operator|+
literal|", "
operator|+
literal|"prefixes="
operator|+
name|RamUsageTester
operator|.
name|humanSizeOf
argument_list|(
name|dic
operator|.
name|prefixes
argument_list|)
operator|+
literal|", "
operator|+
literal|"suffixes="
operator|+
name|RamUsageTester
operator|.
name|humanSizeOf
argument_list|(
name|dic
operator|.
name|suffixes
argument_list|)
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|testOneDictionary
specifier|public
name|void
name|testOneDictionary
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|toTest
init|=
literal|"hungarian_dictionary-1.6.1.1-fx+tb+sm+fn.xpi"
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
name|tests
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|tests
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|toTest
argument_list|)
condition|)
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|DICTIONARY_HOME
argument_list|,
name|tests
index|[
name|i
index|]
argument_list|)
decl_stmt|;
assert|assert
name|f
operator|.
name|exists
argument_list|()
assert|;
try|try
init|(
name|ZipFile
name|zip
init|=
operator|new
name|ZipFile
argument_list|(
name|f
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
init|)
block|{
name|ZipEntry
name|dicEntry
init|=
name|zip
operator|.
name|getEntry
argument_list|(
name|tests
index|[
name|i
operator|+
literal|1
index|]
argument_list|)
decl_stmt|;
assert|assert
name|dicEntry
operator|!=
literal|null
assert|;
name|ZipEntry
name|affEntry
init|=
name|zip
operator|.
name|getEntry
argument_list|(
name|tests
index|[
name|i
operator|+
literal|2
index|]
argument_list|)
decl_stmt|;
assert|assert
name|affEntry
operator|!=
literal|null
assert|;
try|try
init|(
name|InputStream
name|dictionary
init|=
name|zip
operator|.
name|getInputStream
argument_list|(
name|dicEntry
argument_list|)
init|;                InputStream affix = zip.getInputStream(affEntry)
block|)
block|{
operator|new
name|Dictionary
argument_list|(
name|affix
argument_list|,
name|dictionary
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class
unit|}
end_unit
