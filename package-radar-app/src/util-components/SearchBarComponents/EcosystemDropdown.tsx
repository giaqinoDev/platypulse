import "./EcosystemDropdown.css"
import { useEffect, useRef, useState } from "react";
import { EcosystemsList } from "./EcosystemsList";
import type { RegistrySearchContextStructure } from "../../contexts/RegistrySearch/RegistrySearchContextStructure";
interface SearchContextProp{
    searchContext: RegistrySearchContextStructure|null
}
function EcosystemDropdown({ searchContext } : SearchContextProp){
    const [showDropdown, setShowDropdown] = useState<boolean>(false);
    const [activeOptionId, setActiveOptionId] = useState<number>(1);
    const [ecosystemOptions] = useState(new EcosystemsList().getEcosystemOptions());
    const dropdown_menuRef = useRef<HTMLDivElement>(null)
    function dropDownState(event: MouseEvent){
        if(dropdown_menuRef.current && !dropdown_menuRef.current.contains(event.target as Node)){
            setShowDropdown(false);
        }
    }

    useEffect(() => {
        document.addEventListener("click", dropDownState);

        return () => {
            document.removeEventListener("click", dropDownState);
        }
    }, [])
    return(
        <div className="ecosystem-dropdown-container" onClick={() => {setShowDropdown(!showDropdown)}} ref={dropdown_menuRef}>
            <div>
                <img src={ecosystemOptions.at(activeOptionId-1)?.icon_path} className="ecosystem-icon-active"/>
            </div>
            {showDropdown &&
                <div className="ecosystem-dropdown">
                    {ecosystemOptions.map((ecosystem) => {
                        return(
                            <div 
                                key={ecosystem.id} 
                                className="ecosystem-option" 
                                onClick={() => {
                                    setActiveOptionId(ecosystem.id)}
                                }>
                                <img src={ecosystem.icon_path} className="ecosystem-icon"/>
                                <p className="ecosystem-name">{ecosystem.name}</p>
                            </div>
                        );
                    })}
                </div>
            }
        </div>
    );
}

export default EcosystemDropdown;