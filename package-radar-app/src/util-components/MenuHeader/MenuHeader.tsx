import platyPulse_svg from "../../../public/Platypulse_logo.svg"
import "./MenuHeader.css"

function MenuHeader(){
    return(
        <div className="menu-header-container">
            <img src={platyPulse_svg} className="platy-pulse-svg"></img>
        </div>
    );
}

export default MenuHeader;