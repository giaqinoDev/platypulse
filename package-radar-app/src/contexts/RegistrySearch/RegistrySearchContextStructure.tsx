import type { Dispatch, SetStateAction } from "react";
import type { SearchContextStructure } from "./SearchContextStructure";

export interface RegistrySearchContextStructure{
    registrySearchTerm: SearchContextStructure | null,
    setRegistrySearchTerm: Dispatch<SetStateAction<SearchContextStructure | null>>
}