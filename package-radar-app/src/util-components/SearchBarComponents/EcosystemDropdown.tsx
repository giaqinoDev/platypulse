import { SiNpm } from "react-icons/si";
import { SiPypi } from "react-icons/si";
import { TbBrandYarn } from "react-icons/tb";


import "./EcosystemDropdown.css"
import { useEffect, useRef, useState } from "react";
function EcosystemDropdown(){
    const [showDropdown, setShowDropdown] = useState<boolean>(false);
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
            <SiNpm className="ecosystem-icon"/>

            {showDropdown &&
                <div className="ecosystem-dropdown">
                    <SiPypi className="ecosystem-icon"/>
                    <TbBrandYarn className="ecosystem-icon"/>
                </div>
            }
        </div>
    );
}

export default EcosystemDropdown;