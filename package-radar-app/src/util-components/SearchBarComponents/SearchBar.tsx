import { IoSearchOutline } from "react-icons/io5";
import EcosystemDropdown from "./EcosystemDropdown";
import { useState } from "react";
import "./SearchBar.css"
import { EcosystemsList } from "./EcosystemsList";
import { useNavigate } from "react-router";

function SearchBar(){
    const [searchTerm, setSearchTerm] = useState<string>("");
    //const regSearchContext = useContext(RegistrySearchContext);
    const ecosystemOptions = new EcosystemsList().getEcosystemOptions();
    const [activeOptionId, setActiveOptionId] = useState<number>(1);

    const navigate = useNavigate();

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
                if(query.search_term.length == 2){
                    navigate(`/package/${query.ecosystem}/${query.search_term[0]}/${query.search_term[1]}`);
                }
                else if(query.search_term.length == 1){
                    navigate(`/package/${query.ecosystem}/${query.search_term[0]}`);
                }
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