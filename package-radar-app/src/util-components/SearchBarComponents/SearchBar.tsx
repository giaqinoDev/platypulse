import { IoSearchOutline } from "react-icons/io5";
import EcosystemDropdown from "./EcosystemDropdown";
import { useState, type Dispatch, type SetStateAction } from "react";
import type { SearchContextStructure } from "../../contexts/RegistrySearch/SearchContextStructure";

interface QueryStructure{
    onSearch: Dispatch<SetStateAction<SearchContextStructure | null>>;
}
function SearchBar({ onSearch } : QueryStructure){
    const [searchTerm, setSearchTerm] = useState<string>("");

    function parseSearchTerm(searchTerm : string){
        const parsedSearchTerm = searchTerm.split("/");
        return {
            search_term: parsedSearchTerm,
            ecosystem: "npm"
        };
    }

    return (
        <form onSubmit={(event) => {
                event.preventDefault();
                const query = parseSearchTerm(searchTerm.trim());
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
            </div>
            <EcosystemDropdown/>
        </form>
    );
}
export default SearchBar;