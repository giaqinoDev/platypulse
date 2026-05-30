import { createContext } from "react";
interface searchContextStructure{
    search_term: string,
    ecosystem: string,
}
const searchRegistryContext = createContext<searchContextStructure|null>(null);

export default searchRegistryContext;