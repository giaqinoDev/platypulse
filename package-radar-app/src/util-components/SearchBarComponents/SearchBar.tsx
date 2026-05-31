import { IoSearchOutline } from "react-icons/io5";
import EcosystemDropdown from "./EcosystemDropdown";
import { useContext, useState, type Dispatch, type SetStateAction } from "react";
import type { SearchContextStructure } from "../../contexts/RegistrySearch/SearchContextStructure";
import RegistrySearchContext from "../../contexts/RegistrySearch/RegistrySearchContext";
import "./SearchBar.css"
import { EcosystemsList } from "./EcosystemsList";

interface QueryStructure{
    onSearch: Dispatch<SetStateAction<SearchContextStructure | null>>;
}
function SearchBar({ onSearch } : QueryStructure){
    const [searchTerm, setSearchTerm] = useState<string>("");
    const regSearchContext = useContext(RegistrySearchContext);
    const ecosystemOptions = new EcosystemsList().getEcosystemOptions();
    const [activeOptionId, setActiveOptionId] = useState<number>(1);

    function constructSearchQuery(searchTerm : string){
        const parsedSearchTerm = searchTerm.split("/");
        const activeOptionName = ecosystemOptions.at(activeOptionId-1)?.name;
        return {
            search_term: parsedSearchTerm,
            ecosystem: activeOptionName
        };
    }

    return (
        <form 
            className="search-container" 
            onSubmit={(event) => {
                event.preventDefault();
                const query = constructSearchQuery(searchTerm.trim());
                if(regSearchContext != null){
                    regSearchContext.setRegistrySearchTerm(query);
                }
                onSearch(query);
            }}>
            <div className="search-field-container">
                <IoSearchOutline className="search-icon"/>
                <input
                    className="package-search-bar" 
                    type="text" 
                    placeholder="Analyze Package..."
                    name="searchTerm" 
                    required
                    value={searchTerm}
                    onChange={(event) => {setSearchTerm(event.target.value)}}/>
                <EcosystemDropdown activeOptionId={activeOptionId} setActiveOptionId={setActiveOptionId}/>
            </div>
        </form>
    );
}
export default SearchBar;