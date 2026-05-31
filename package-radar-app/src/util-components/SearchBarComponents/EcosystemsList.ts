import npmIcon from "../../assets/SearchBarAssets/npm-icon.svg"
import pypiIcon from "../../assets/SearchBarAssets/pypi-icon.svg"
import yarnIcon from "../../assets/SearchBarAssets/yarn-icon.svg"

export class EcosystemsList{
    #ecosystems = [
        {
            id: 1,
            name: "npm",
            icon_path: npmIcon
        },
        {
            id: 2,
            name: "pypi",
            icon_path: pypiIcon
        },
        {
            id: 3,
            name: "yarn",
            icon_path: yarnIcon
        },
    ]

    getEcosystemOptions(){
        return this.#ecosystems
    }
}