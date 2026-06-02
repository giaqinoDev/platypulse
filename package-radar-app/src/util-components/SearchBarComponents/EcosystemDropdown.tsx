import "./EcosystemDropdown.css"
import { useEffect, useRef, useState, type Dispatch, type SetStateAction } from "react";
import { EcosystemsList } from "./EcosystemsList";

interface ActiveOptionProp{
    activeOptionId: number,
    setActiveOptionId: Dispatch<SetStateAction<number>>;
}
function EcosystemDropdown({ activeOptionId, setActiveOptionId }: ActiveOptionProp){
    const [showDropdown, setShowDropdown] = useState<boolean>(false);
    const ecosystemOptionsConst = new EcosystemsList().getEcosystemOptions();
    const [ecosystemOptions] = useState(ecosystemOptionsConst);
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
            <img src={ecosystemOptions.at(activeOptionId-1)?.icon_path} className="ecosystem-icon-active"/>
            
            {showDropdown && 
                <div className="ecosystem-dropdown">
                    {ecosystemOptions.map((ecosystem) => {
                        if(ecosystem.id != activeOptionId){
                            return(
                            <div 
                                key={ecosystem.id} 
                                className="ecosystem-option" 
                                onClick={() => {
                                    setActiveOptionId(ecosystem.id)
                                }}>
                                <img src={ecosystem.icon_path} className="ecosystem-icon"/>
                                <p className="ecosystem-name">{ecosystem.name}</p>
                            </div>
                        );
                        }
                    })}
                </div>
            }
        </div>
    );
}

export default EcosystemDropdown;