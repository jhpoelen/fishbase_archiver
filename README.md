# fishbase_archiver
Generates tabular archives for [FishBase](http://fishbase.org) and [SeaLifeBase](http://sealifebase.org) tables using [rOpenSci's FishBase API](https://github.com/ropensci/fishbaseapi). Precompiled archives can be found in the [releases](../../releases) section.

## prerequisites
  * linux, mac (tested on mac os x 10.11.5)
  * bash / curl
  * jq (see https://stedolan.github.io/jq/)

## usage
Execute ```./archive_fishbase.sh``` to compile the fishbase archives. 

## expected results 
You'll see output like:

```

[https://fishbase.ropensci.org/stocks?offset=0&limit=5000] downloading...
[https://fishbase.ropensci.org/stocks?offset=0&limit=5000] downloaded.
returned: 5000, offset= 5000
[https://fishbase.ropensci.org/stocks?offset=5000&limit=5000] downloading...
[https://fishbase.ropensci.org/stocks?offset=5000&limit=5000] downloaded.
returned: 5000, offset= 10000
```

After the script finishes, you'll find some archives in your work directory. Some examples look like: 

```
sealifebase_species.tsv.gz
sealifebase_stocks.tsv.gz
sealifebase_taxa.tsv.gz
species.tsv.gz
stocks.data.tsv.gz
stocks.head.tsv.gz
stocks.tsv.gz
taxa.tsv.gz
```

The archives are gzip compressed, tab separated files. The first few lines of ```stocks.tsv.gz``` look like:

```tsv
StockCode	SpecCode	StockDefs	StockDefsGeneral	Level	LocalUnique	IUCN_Code	IUCN_Assessment	Protected	StocksRefNo	CITES_Code	CITES_Date	CITES_Ref	CITES_Remarks	Northernmost	NorthSouthN	Southermost	NorthSouthS	Westernmost	WestEastW	Easternmost	WestEastE	BoundingRef	BoundingMethod	TempMin	TempMax	TempRef	EnvTemp	Resilience	ResilienceRemark	pHMin	pHMax	pHRef	dHMin	dHMax	dHRef	GenBankID	RfeID	FIGIS_ID	EcotoxID	SCRFA_data	GMAD_ID	SAUP	SAUP_ID	SAUP_Group	AusMuseum	FishTrace	IUCN_ID	IUCN_Group	BOLD_ID	IGFAName	EssayID	ICESStockID	OsteoBaseID	DORIS_ID	Aquamaps	Morphology	Occurrence	Strains	Ecology	Diseases	Abnorm	Metabolism	Predators	Spawning	Fecundity	Speed	Diet	Eggs	EggDevelop	Food	Larvae	LarvDyn	LarvSpeed	PopDyn	LengthWeight	Gillarea	Maturity	MatSizes	Processing	Reproduction	Introductions	Abundance	Vision	Genetics	Aquaculture	CountryComp	Allele	GeneticStudies	Ration	Foods	Ecotoxicology	Brains	Catches	FAOAqua	LengthRelations	LengthFrequency	Sounds	Broodstock	EggNursery	FryNursery	LarvalNursery	Entered	DateEntered	Modified	DateModified	Expert	DateChecked	TS
1884	6	Atlantic, Indian and Pacific:  in tropical and subtropical waters. Highly migratory species, Annex I of the 1982 Convention on the Law of the Sea (Ref. 26139).	Atlantic, Indian and Pacific	species in general	Atlantic, Indian and Pacific:  in tropical and sub	LC	null	0	6376	null	null	null	null	47	N	38	S	180	W	180	E	54341	1	21	30	26	subtropical	High	K=0.4-1.2; tm<1; tmax=5; Fec=85,000	null	null	null	null	null	null	34814	mh	3130	null	0	null	1	600006	1	384	0	154712	null	13788	Dolphinfish	null	null	194	null	1	1	1	0	1	1	1	1	1	1	1	1	1	1	1	1	1	0	0	1	1	1	1	1	1	1	0	1	1	0	0	1	1	0	1	1	0	1	1	0	1	1	0	1	1	1	1	2	1990-10-18T00:00:00.000Z	949	2014-10-14T00:00:00.000Z	133	1996-02-27T00:00:00.000Z	null
```
