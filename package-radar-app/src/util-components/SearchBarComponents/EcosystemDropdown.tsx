import { SiNpm } from "react-icons/si";
import { SiPypi } from "react-icons/si";
import { TbBrandYarn } from "react-icons/tb";


import "./EcosystemDropdown.css"
import { useState } from "react";
function EcosystemDropdown(){
    const [showDropdown, setShowDropdown] = useState<boolean>(false);
    return(
        <div className="ecosystem-dropdown-container" onClick={() => {setShowDropdown(!showDropdown)}}>
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