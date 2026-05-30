import { createContext } from "react";
import type { RegistrySearchContextStructure } from "./RegistrySearchContextStructure";

const RegistrySearchContext = createContext<RegistrySearchContextStructure|null>(null);

export default RegistrySearchContext;