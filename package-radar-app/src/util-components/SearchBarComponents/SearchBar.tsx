import { IoSearchOutline } from "react-icons/io5";
import EcosystemDropdown from "./EcosystemDropdown";
import { useEffect, useRef, useState } from "react";
import "./SearchBar.css"
import { EcosystemsList } from "./EcosystemsList";
import { useLocation, useNavigate } from "react-router";

function SearchBar(){
    const [searchTerm, setSearchTerm] = useState<string>("");
    //const regSearchContext = useContext(RegistrySearchContext);
    const formRef = useRef<HTMLFormElement>(null);
    const ecosystemOptions = new EcosystemsList().getEcosystemOptions();
    const [activeOptionId, setActiveOptionId] = useState<number>(1);
    const location = useLocation();
    const navigate = useNavigate();

    function constructSearchQuery(searchTerm : string){
        const parsedSearchTerm = searchTerm.split("/");
        const activeOptionName = ecosystemOptions.at(activeOptionId-1)?.name;
        return {
            search_term: parsedSearchTerm,
            ecosystem: activeOptionName
        };
    }

    function handleIconClick(){
        if(formRef.current){
            formRef.current.requestSubmit();
        }
    }

    useEffect(() => {
        if(location.pathname.includes("package/npm/")){
            setActiveOptionId(1);
        }
        else if(location.pathname.includes("package/pypi/")){
            setActiveOptionId(2);
        }
        else if(location.pathname.includes("package/yarn/")){
            setActiveOptionId(3);
        }
    }, []);

    return (
        <form 
            className="search-container"
            ref={formRef} 
            onSubmit={(event) => {
                event.preventDefault();
                const query = constructSearchQuery(searchTerm.trim());
                if(query.search_term.length == 2){
                    navigate(`/package/${query.ecosystem}/${query.search_term[0].replace("@", "")}/${query.search_term[1]}/latest`);
                }
                else if(query.search_term.length == 1){
                    navigate(`/package/${query.ecosystem}/${query.search_term[0]}/latest`);
                }
            }}>
            <div className="search-field-container">
                <IoSearchOutline className="search-icon" onClick={() => {handleIconClick()}}/>
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