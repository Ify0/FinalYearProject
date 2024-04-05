package com.example.finalyear;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

public class ParsedExtraAdapter extends RecyclerView.Adapter<ParsedExtraAdapter.ViewHolder> {
    private List<String> parsedExtra;
    private HashMap<String, Integer> ingredientColors;
    private static final int RED_COLOR = Color.parseColor("#D75722");
    private static final int ORANGE_COLOR = Color.parseColor("#D79322");
    private static final int YELLOW_COLOR = Color.parseColor("#D7B422");
    public ParsedExtraAdapter(List<String> parsedExtra) {
        this.parsedExtra = parsedExtra;
        this.ingredientColors = createIngredientColorMapping();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_parsed_extra, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //holder.bind(parsedExtra.get(position));
        String ingredient = parsedExtra.get(position);
        holder.bind(ingredient, ingredientColors);
    }

    @Override
    public int getItemCount() {
        return parsedExtra.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewParsedExtra);
        }

        public void bind(String ingredient, HashMap<String, Integer> ingredientColors) {
            textView.setText(ingredient);

            // Apply color based on the ingredient name
            if (ingredientColors.containsKey(ingredient)) {
                textView.setTextColor(ingredientColors.get(ingredient));
            }
        }
    }

    // Method to create a mapping of ingredient names to colors
    private HashMap<String, Integer> createIngredientColorMapping() {
        HashMap<String, Integer> mapping = new HashMap<>();
        // Add your ingredient-color mappings here
        mapping.put("Oxybenzone", RED_COLOR);
        mapping.put("Avobenzone", RED_COLOR);
        mapping.put("Benzalkonium Chloride", RED_COLOR);
        mapping.put("Lead", RED_COLOR);
        mapping.put("Octinoxate", RED_COLOR);
        mapping.put("Methylisothiazolinone ", RED_COLOR);
        mapping.put("Methylchloroisothiazolinone", RED_COLOR);
        mapping.put("Hydroquinone", RED_COLOR);
        mapping.put("Ethanolamines", RED_COLOR);
        mapping.put("Carbon Black", RED_COLOR);
        mapping.put("Triclosan ", RED_COLOR);
        mapping.put("Triclocarban", RED_COLOR);
        mapping.put("Toluene", RED_COLOR);
        mapping.put("Formaldehyde ", RED_COLOR);
        mapping.put("propylparaben ", RED_COLOR);
        mapping.put("isobutyl paraben", RED_COLOR);
        mapping.put("Methylparaben", RED_COLOR);
        mapping.put("Homosalate", RED_COLOR);
        mapping.put("Sulfates", RED_COLOR);
        mapping.put("Parabens", RED_COLOR);
        mapping.put("Phthalates", RED_COLOR);
        mapping.put("1,2,4-TRIHYDROXYBENZENE", RED_COLOR);
        mapping.put("2-BROMO-2-NITROPROPANE-1,3-DIOL", RED_COLOR);
        mapping.put("2-CHLORO-P-PHENYLENEDIAMINE  ", RED_COLOR);
        mapping.put("2-CHLORO-P-PHENYLENEDIAMINE SULFATE", RED_COLOR);
        mapping.put("3-BENZYLIDENE CAMPHOR", RED_COLOR);
        mapping.put("4,4'-ISOPROPYLIDENEDIPHENOL/EPICHLOROHYDRIN COPOLYMER", RED_COLOR);
        mapping.put("4-AMINO-2-NITRODIPHENYLAMINE-2'-CARBOXYLIC ACID", RED_COLOR);
        mapping.put("4-METHYLBENZYLIDENE CAMPHOR", RED_COLOR);
        mapping.put("5-BROMO-5-NITRO-1,3-DIOXANE", RED_COLOR);
        mapping.put("6-AMINO-M-CRESOL", RED_COLOR);
        mapping.put("ACETALDEHYDE", RED_COLOR);
        mapping.put("ACETYL CEDRENE", RED_COLOR);
        mapping.put("ACETYL HEXAMETHYL TETRALIN", RED_COLOR);
        mapping.put("ALUMINUM CHLOROHYDRATE", RED_COLOR);
        mapping.put("ALUMINUM ZIRCONIUM OCTACHLOROHYDREX GLY", RED_COLOR);
        mapping.put("ALUMINUM ZIRCONIUM PENTACHLOROHYDRATE", RED_COLOR);
        mapping.put("ALUMINUM ZIRCONIUM TETRACHLOROHYDREX GLY", RED_COLOR);
        mapping.put("ALUMINUM ZIRCONIUM TRICHLOROHYDREX GLY", RED_COLOR);
        mapping.put("AZADIRACHTA INDICA SEED EXTRACT", RED_COLOR);
        mapping.put("BENZOPHENONE", RED_COLOR);
        mapping.put("BENZOTRIAZOLE ", RED_COLOR);
        mapping.put("BENZYLHEMIFORMAL", RED_COLOR);
        mapping.put("BORIC ACID", RED_COLOR);
        mapping.put("BUTYLPARABEN", RED_COLOR);
        mapping.put("BUTYLPHENYL METHYLPROPIONAL", RED_COLOR);
        mapping.put("Phenoxyethanol", ORANGE_COLOR);
        mapping.put("Dimethicone", ORANGE_COLOR);
        mapping.put("Disodium EDTA", ORANGE_COLOR);
        mapping.put("BRASSICAMIDOPROPYL DIMETHYLAMINE", ORANGE_COLOR);
        mapping.put("BENZOPHENONE-6", ORANGE_COLOR);
        mapping.put("BENZOPHENONE-9", ORANGE_COLOR);
        mapping.put("AZIRIDINE HOMOPOLYMER ETHOXYLATED", ORANGE_COLOR);
        mapping.put("AZURANTS OPTIQUES", ORANGE_COLOR);
        mapping.put("AMODIMETHICONE", ORANGE_COLOR);
        mapping.put("AMMONIUM POLYACRYLOYLDIMETHYL TAURATE", ORANGE_COLOR);
        mapping.put("AMMONIUM SILVER ZINC ALUMINUM SILICATE", ORANGE_COLOR);
        mapping.put("AMMONIUM HYDROXIDE", ORANGE_COLOR);
        mapping.put("AMMONIUM C6-16 PERFLUOROALKYLETHYL PHOSPHATE", ORANGE_COLOR);
        mapping.put("AMMONIUM COCO-SULFATE", ORANGE_COLOR);
        mapping.put("AMMONIUM ACRYLATE", ORANGE_COLOR);
        mapping.put("AMMONIUM ACRYLOYLDIMETHYLTAURATE/CARBOXYETHYL ACRYLATE CROSSPOLYMER", ORANGE_COLOR);
        mapping.put("ALUMINUM/MAGNESIUM HYDROXIDE STEARATE", ORANGE_COLOR);
        mapping.put("ALUMINUM SUCROSE OCTASULFATE", ORANGE_COLOR);
        mapping.put("ALUMINUM TRIFORMATE", ORANGE_COLOR);
        mapping.put("ALUMINUM LACTATE", ORANGE_COLOR);
        mapping.put("ALUMINUM SILICATE", ORANGE_COLOR);
        mapping.put("ALUMINUM STARCH OCTENYLSUCCINATE", ORANGE_COLOR);
        mapping.put("ALUMINUM BENZOATE", ORANGE_COLOR);
        mapping.put("ALUMINUM CALCIUM SODIUM SILICATE", ORANGE_COLOR);
        mapping.put("ALUMINUM DIMYRISTATE", ORANGE_COLOR);
        mapping.put("ALUMINUM DISTEARATE", ORANGE_COLOR);
        mapping.put("ALUMINUM FLUORIDE", ORANGE_COLOR);
        mapping.put("ALUMINUM GLYCINATE", ORANGE_COLOR);
        mapping.put("ALIPHATIC URETHANE ACRYLATE", ORANGE_COLOR);
        mapping.put("ALIPHATIC URETHANE METHACRYLATE", ORANGE_COLOR);
        mapping.put("ALKYL DIMETHYL BETAINE", ORANGE_COLOR);
        mapping.put("ACRYLATES/C10-30 ALKYL ACRYLATE CROSSPOLYMER", ORANGE_COLOR);
        mapping.put("ACRYLATES/METHACRYLOYLOXYETHYL PHOSPHATE COPOLYMER", ORANGE_COLOR);
        mapping.put("ACRYLIC ACID", ORANGE_COLOR);
        mapping.put("ACID YELLOW 23 ALUMINUM LAKE ", ORANGE_COLOR);
        mapping.put("ACID BLUE 9 ALUMINUM LAKE", ORANGE_COLOR);
        mapping.put("2-METHOXYMETHYL-P-PHENYLENEDIAMINE", ORANGE_COLOR);
        mapping.put(" 2-AMINO-4-HYDROXYETHYLAMINOANISOLE SULFATE", ORANGE_COLOR);
        mapping.put("2,4-DIAMINOPHENOXYETHANOL SULFATE", ORANGE_COLOR);
        mapping.put("1-HYDROXYETHYL 4,5-DIAMINO PYRAZOLE SULFATE", ORANGE_COLOR);
        mapping.put("1,4-BENZENEDICARBOXYLIC ACID,1,4-DIMETHYL ESTER,POLYMER:1 ", ORANGE_COLOR);
        mapping.put("BEHENAMIDOPROPYL DIMETHYLAMINE", ORANGE_COLOR);
        mapping.put("BEHENTRIMONIUM CHLORIDE", ORANGE_COLOR);
        mapping.put("BEHENTRIMONIUM METHOSULFATE", ORANGE_COLOR);
        mapping.put("BENZALKONIUM BROMIDE", ORANGE_COLOR);
        mapping.put("BENZALKONIUM CHLORIDE", ORANGE_COLOR);
        mapping.put("BENZETHONIUM CHLORIDE", ORANGE_COLOR);
        mapping.put("BENZISOTHIAZOLINONE", ORANGE_COLOR);
        mapping.put("BENZYLIDENE CAMPHOR SULFONIC ACID ", ORANGE_COLOR);
        mapping.put("BHA", ORANGE_COLOR);
        mapping.put("BHT", ORANGE_COLOR);
        mapping.put("BIS(PENTAERYTHRITYL TRIACRYLATE) PENTAERYTHRITYL DIACRYLATE/IPDI COPOLYMER", ORANGE_COLOR);
        mapping.put("BIS-(ISOSTEAROYL/OLEOYL/ISOPROPYL) DIMONIUM METHOSULFATE", ORANGE_COLOR);
        mapping.put("BIS-4-PCA DIMETHICONE", ORANGE_COLOR);
        mapping.put("BIS-ETHYLHEXYL POLY(1,4-BUTANEDIOL)-13/IPDI COPOLYMER", ORANGE_COLOR);
        mapping.put("BIS-ETHYLHEXYL POLY(CAPROLACTONE NEOPENTYL GLYCOL)/IPDI COPOLYMER", ORANGE_COLOR);
        mapping.put("BIS-ETHYLHEXYLOXYPHENOL METHOXYPHENYL TRIAZINE", ORANGE_COLOR);
        mapping.put("BIS-HEA POLY(1,4-BUTANEDIOL)-9/IPDI COPOLYMER", ORANGE_COLOR);
        mapping.put("BUMETRIZOLE", ORANGE_COLOR);
        mapping.put("BUTYL METHOXYDIBENZOYLMETHANE", ORANGE_COLOR);
        mapping.put("BUTYLATED PVP", ORANGE_COLOR);
        mapping.put("BUTYLOCTYL SALICYLATE", ORANGE_COLOR);
        mapping.put("Perfume", YELLOW_COLOR);
        mapping.put("Linalool", YELLOW_COLOR);
        mapping.put("Limonene", YELLOW_COLOR);
        mapping.put("Sodium benzoate", YELLOW_COLOR);
        mapping.put("Potassium sorbate", YELLOW_COLOR);
        mapping.put("Benzyl alcohol", YELLOW_COLOR);
        mapping.put("Propylene Glycol", YELLOW_COLOR);
        mapping.put("Sodium hydroxide", YELLOW_COLOR);
        mapping.put("CI 77891", YELLOW_COLOR);
        mapping.put("CITRONELLOL", YELLOW_COLOR);
        mapping.put("GERANIOL", YELLOW_COLOR);
        mapping.put("CI 77491", YELLOW_COLOR);
        mapping.put("CAPRYLIC/CAPRIC TRIGLYCERIDE", YELLOW_COLOR);
        mapping.put("BUTYROSPERMUM PARKII BUTTER", YELLOW_COLOR);
        mapping.put("CAPRYLYL GLYCOL", YELLOW_COLOR);
        mapping.put("CI 77499", YELLOW_COLOR);
        mapping.put("HEXYL CINNAMAL", YELLOW_COLOR);
        mapping.put("HELIANTHUS ANNUUS SEED OIL", YELLOW_COLOR);
        mapping.put("CI 77492", YELLOW_COLOR);
        mapping.put("BENZYL SALICYLATE", YELLOW_COLOR);
        mapping.put("CI 19140", YELLOW_COLOR);
        mapping.put("CI 19140", YELLOW_COLOR);
        mapping.put("BUTYLENE GLYCOL", YELLOW_COLOR);
        mapping.put("BUTYL BENZOATE", YELLOW_COLOR);
        mapping.put("BUTANE", YELLOW_COLOR);
        mapping.put("BUTOXYDIGLYCOL", YELLOW_COLOR);
        mapping.put("BUTOXYETHANOL", YELLOW_COLOR);
        mapping.put("BROMOCHLOROPHENE", YELLOW_COLOR);
        mapping.put("BROMOCRESOL GREEN", YELLOW_COLOR);
        mapping.put("BROMOTHYMOL BLUE", YELLOW_COLOR);
        mapping.put("BETA-CARYOPHYLLENE", YELLOW_COLOR);
        mapping.put("BENZOYL PEROXIDE", YELLOW_COLOR);
        mapping.put("BENZYL ALCOHOL", YELLOW_COLOR);
        mapping.put("BENZYL BENZOATE", YELLOW_COLOR);
        mapping.put("BENZYL CINNAMATE", YELLOW_COLOR);
        mapping.put("BENZYL SALICYLATE", YELLOW_COLOR);
        mapping.put("BENZOIC ACID", YELLOW_COLOR);
        mapping.put("BENZALKONIUM SACCHARINATE", YELLOW_COLOR);
        mapping.put("BENZALDEHYDE", YELLOW_COLOR);
        mapping.put("1,3-BIS-(2,4-DIAMINOPHENOXY)PROPANE", YELLOW_COLOR);
        mapping.put("1,3-BIS-(2,4-DIAMINOPHENOXY)PROPANE HCL", YELLOW_COLOR);
        mapping.put("1,5-NAPHTHALENEDIOL", YELLOW_COLOR);
        mapping.put("1-HEXYL 4,5-DIAMINO PYRAZOLE SULFATE", YELLOW_COLOR);
        mapping.put("1-NAPHTHOL", YELLOW_COLOR);
        mapping.put("2,2'-METHYLENEBIS 4-AMINOPHENOL", YELLOW_COLOR);
        mapping.put("ALPHA-DAMASCONE", YELLOW_COLOR);
        mapping.put("ALPHA-ISOMETHYL IONONE", YELLOW_COLOR);
        mapping.put("AVENA SATIVA KERNEL OIL", YELLOW_COLOR);
        mapping.put("AVENA SATIVA KERNEL EXTRACT", YELLOW_COLOR);
        mapping.put("ARACHIS HYPOGAEA FRUIT EXTRACT", YELLOW_COLOR);
        mapping.put("AMMONIUM SULFATE ", YELLOW_COLOR);
        mapping.put("AMMONIA", YELLOW_COLOR);
        mapping.put("ALCOHOL DENAT", YELLOW_COLOR);
        mapping.put("ALCOHOL", YELLOW_COLOR);
        mapping.put("ALPHA-TERPINENE", YELLOW_COLOR);
        mapping.put("ALPHA-PINENES", YELLOW_COLOR);
        // Add more mappings as needed
        return mapping;
    }

    }


